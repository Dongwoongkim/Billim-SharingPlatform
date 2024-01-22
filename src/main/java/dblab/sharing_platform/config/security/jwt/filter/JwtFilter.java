package dblab.sharing_platform.config.security.jwt.filter;

import static dblab.sharing_platform.config.security.jwt.provider.TokenProvider.ACCESS;

import dblab.sharing_platform.config.security.jwt.provider.TokenProvider;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String REFRESH_HEADER = "Auth";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String EXCEPTION_PATH = "/exception";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String accessToken = extractTokenFromRequest(request, AUTH_HEADER);
        String refreshToken = request.getHeader(REFRESH_HEADER);

        if (request.getRequestURI().startsWith(LOGIN_PATH)) {
            chain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().startsWith(EXCEPTION_PATH)) {
            chain.doFilter(request, response);
            return;
        }

        if (validateExpire(accessToken) && validateExpire(refreshToken)) {
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthenticationFromToken(accessToken));
        } else if (!validateExpire(accessToken) && validateExpire(refreshToken)) {
            if (tokenProvider.validateRefreshToken(refreshToken)) {
                Authentication authentication = tokenProvider.getAuthenticationFromToken(refreshToken);
                String newAccessToken = tokenProvider.createToken(authentication, ACCESS);
                Authentication newAuthentication = tokenProvider.getAuthenticationFromToken(newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);

                response.setHeader(AUTH_HEADER, newAccessToken);
            }
        } else if (validateExpire(accessToken) && !validateExpire(refreshToken)) {
            if (tokenProvider.validateToken(accessToken)) {
                Authentication authentication = tokenProvider.getAuthenticationFromToken(accessToken);
                tokenProvider.reIssueRefreshToken(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }


    private String extractTokenFromRequest(HttpServletRequest request, String header) {
        String Bearer_token = request.getHeader(header);
        return extractToken(Bearer_token);
    }

    private String extractToken(String bearer_token) {
        if (StringUtils.hasText(bearer_token) && bearer_token.startsWith(BEARER)) {
            return bearer_token.substring(7);
        }
        return null;
    }

    private boolean validateExpire(String token) {
        return StringUtils.hasText(token) && tokenProvider.validateExpire(token);
    }
}
