package dblab.sharing_platform.config.security.jwt.provider;

import dblab.sharing_platform.config.security.details.MemberDetails;
import dblab.sharing_platform.domain.refresh.RefreshToken;
import dblab.sharing_platform.exception.auth.ValidateTokenException;
import dblab.sharing_platform.exception.token.TokenNotFoundException;
import dblab.sharing_platform.repository.refresh.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TokenProvider {

    private static final String AUTH_KEY = "AUTHORITY";
    private static final String AUTH_ID = "ID";
    private static final String AUTH_USERNAME = "USERNAME";
    public static final String ACCESS = "accessToken";
    public static final String REFRESH = "refreshToken";

    private final long tokenValidityMilliSeconds;
    private final long refreshTokenValidityMilliSeconds;
    private final String originSecretKey;
    private final RefreshTokenRepository refreshTokenRepository;
    private Key secretKey;

    public TokenProvider(@Value("${jwt.token-validity-in-seconds}") long tokenValiditySeconds,
                         @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityMilliSeconds,
                         @Value("${jwt.secret_key}") String originSecretKey, RefreshTokenRepository tokenRepository) {
        this.tokenValidityMilliSeconds = tokenValiditySeconds * 1000;
        this.refreshTokenValidityMilliSeconds = refreshTokenValidityMilliSeconds * 1000;
        this.originSecretKey = originSecretKey;
        this.refreshTokenRepository = tokenRepository;
    }

    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Decoders.BASE64.decode(originSecretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication, String type) {
        MemberDetails principal = (MemberDetails) authentication.getPrincipal();

        String authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityMilliSeconds);

        if (type.equals(ACCESS)) {
            validity = new Date(now + this.tokenValidityMilliSeconds);
        } else if (type.equals(REFRESH)) {
            validity = new Date(now + this.refreshTokenValidityMilliSeconds);
        }

        return Jwts.builder()
                .addClaims(Map.of(AUTH_ID, principal.getId()))
                .addClaims(Map.of(AUTH_USERNAME, principal.getUsername()))
                .addClaims(Map.of(AUTH_KEY, authorities))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    @Transactional
    public void reIssueRefreshToken(Authentication authentication) {
        RefreshToken token = refreshTokenRepository.findByUsername(authentication.getName())
                .orElseThrow(TokenNotFoundException::new);
        String newRefreshToken = createToken(authentication, REFRESH);
        token.changeToken(newRefreshToken);
    }

    public Authentication getAuthenticationFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> authorities = Arrays.asList(claims.get(AUTH_KEY).toString().split(","));

        List<? extends GrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                .map(auth -> new SimpleGrantedAuthority(auth))
                .collect(Collectors.toList());

        MemberDetails principal = new MemberDetails((String) claims.get(AUTH_ID),
                (String) claims.get(AUTH_USERNAME),
                null,
                simpleGrantedAuthorities,
                Map.of());

        return new UsernamePasswordAuthenticationToken(principal, token, simpleGrantedAuthorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new ValidateTokenException();
        } catch (UnsupportedJwtException e) {
            throw new ValidateTokenException();
        } catch (IllegalArgumentException e) {
            throw new ValidateTokenException();
        }
    }

    public boolean validateExpire(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public Boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }

        Authentication authentication = getAuthenticationFromToken(token);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(authentication.getName());

        return refreshToken.isPresent() && token.equals(refreshToken.get().getToken());
    }
}
