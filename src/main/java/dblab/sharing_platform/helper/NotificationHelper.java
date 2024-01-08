package dblab.sharing_platform.helper;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.notification.NotificationType;
import dblab.sharing_platform.dto.notification.NotificationRequest;
import dblab.sharing_platform.repository.emitter.EmitterRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final EmitterRepository emitterRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void notificationIfSubscribe(Member sendMember, Member member,
                                        NotificationType notificationType, String msg) {
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartsWithByMemberId(
                String.valueOf(member.getId()));
        
        if (!emitters.isEmpty()) {
            NotificationRequest notificationRequest = new NotificationRequest(sendMember.getNickname() + msg,
                    String.valueOf(notificationType), member.getNickname());
            eventPublisher.publishEvent(notificationRequest);
        }
    }
}
