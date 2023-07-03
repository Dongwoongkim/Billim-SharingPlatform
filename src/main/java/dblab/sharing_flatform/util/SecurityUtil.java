package dblab.sharing_flatform.util;

import dblab.sharing_flatform.config.security.details.MemberDetails;
import dblab.sharing_flatform.exception.auth.IllegalAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


@Slf4j
public class SecurityUtil {

    public static Optional<String> getCurrentUsername(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            log.info("Security Context에 인증정보가 없습니다.");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if(principal instanceof MemberDetails){
            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            return Optional.ofNullable(memberDetails.getUsername());
        } else if (authentication.getPrincipal() instanceof String) {
            return Optional.ofNullable((String) authentication.getPrincipal());
        }

        throw new IllegalAuthenticationException();
    }
}
