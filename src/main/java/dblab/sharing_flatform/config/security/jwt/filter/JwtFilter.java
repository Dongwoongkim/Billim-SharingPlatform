package dblab.sharing_flatform.config.security.jwt.filter;

import dblab.sharing_flatform.config.security.jwt.support.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    // 인증/인가가 필요한 요청시 실행하는 필터
    // 토큰 검증 -> 인증정보 SecurityContext에 저장
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = extractTokenFromRequest(httpServletRequest);

        if (httpServletRequest.getRequestURI().startsWith("/exception/invalid-token")) {
            chain.doFilter(request, response);
            return;
        }

        if (StringUtils.hasText(jwt) && tokenProvider.validate(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("인증정보를 Security Context에 저장하였습니다.");
        } else {
            log.debug("유효하지 않은 JWT입니다.");
        }

        chain.doFilter(request, response);
    }


    private String extractTokenFromRequest(HttpServletRequest request) {
        String Bearer_token = request.getHeader(AUTHORIZATION_HEADER);
        return extractToken(Bearer_token);
    }

    private String extractToken(String bearer_token) {
        if (StringUtils.hasText(bearer_token) && bearer_token.startsWith("Bearer ")) {
            return bearer_token.substring(7);
        }
        return null;
    }
}
