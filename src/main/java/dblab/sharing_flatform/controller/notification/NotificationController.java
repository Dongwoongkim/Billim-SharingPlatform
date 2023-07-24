package dblab.sharing_flatform.controller.notification;

import dblab.sharing_flatform.config.security.util.SecurityUtil;
import dblab.sharing_flatform.dto.response.Response;
import dblab.sharing_flatform.exception.auth.AccessDeniedException;
import dblab.sharing_flatform.service.notification.NotificationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @GetMapping(value = "/subscribe/on", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribeOn(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Long memberId = Long.valueOf(SecurityUtil.getCurrentUserId().orElseThrow(AccessDeniedException::new));
        return notificationService.subscribeOn(memberId, lastEventId);
    }

    @ApiOperation(value = "알림 구독 취소", notes = "알림을 구독 취소한다.")
    @GetMapping(value = "/subscribe/off")
    @ResponseStatus(HttpStatus.OK)
    public Response subscribeOff() {
        Long memberId = Long.valueOf(SecurityUtil.getCurrentUserId().orElseThrow(AccessDeniedException::new));
        notificationService.subscribeOff(memberId);
        return Response.success();
    }
}
