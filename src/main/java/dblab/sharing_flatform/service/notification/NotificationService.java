package dblab.sharing_flatform.service.notification;

import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.notification.Notification;
import dblab.sharing_flatform.domain.notification.NotificationType;
import dblab.sharing_flatform.dto.notification.crud.create.NotificationRequestDto;
import dblab.sharing_flatform.dto.notification.crud.create.NotificationResponseDto;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.repository.emitter.EmitterRepositoryImpl;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 100 * 60L;
    private final EmitterRepositoryImpl emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public SseEmitter subscribe(Long memberId, String lastEventId){
        String emitterId = makeTimeIncludeId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        String eventId = makeTimeIncludeId(memberId);

        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + memberId + "]");

        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, memberId, emitterId, emitter);
        }
        return emitter;
    }

    public void unSubscribe(Long memberId){
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartsWithByMemberId(String.valueOf(memberId));
        Iterator<Map.Entry<String, SseEmitter>> iterator = emitters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SseEmitter> entry = iterator.next();
            if (entry.getKey().startsWith(String.valueOf(memberId))){
                entry.getValue().complete();
                iterator.remove();
            }
        }
    }

    public void send(NotificationRequestDto requestDto){
        Member receiver = memberRepository.findByNickname(requestDto.getReceiver()).orElseThrow(MemberNotFoundException::new);

        Notification notification = notificationRepository.save(
                new Notification(
                        requestDto.getContent(),
                        NotificationType.valueOf(requestDto.getNotificationType()),
                        receiver));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartsWithByMemberId(receiverId);
        emitters.forEach((key,emitter) -> {
            emitterRepository.saveEventCache(key, notification);
            sendNotification(emitter, eventId, key, NotificationResponseDto.toDto(notification));
        });
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try{
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        }catch (IOException exception){
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId){
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter){
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartsWithByMemberId(String.valueOf(memberId));

        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0 )
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private static String makeTimeIncludeId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }


}
