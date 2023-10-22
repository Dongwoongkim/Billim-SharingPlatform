package dblab.sharing_platform.service.member;

import dblab.sharing_platform.config.security.jwt.provider.TokenProvider;
import dblab.sharing_platform.domain.emailAuth.EmailAuth;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.refresh.RefreshToken;
import dblab.sharing_platform.domain.role.Role;
import dblab.sharing_platform.domain.role.RoleType;
import dblab.sharing_platform.dto.member.EmailAuthRequest;
import dblab.sharing_platform.dto.member.LogInResponse;
import dblab.sharing_platform.dto.member.LoginRequest;
import dblab.sharing_platform.dto.member.MemberCreateRequest;
import dblab.sharing_platform.dto.member.OAuth2MemberCreateRequest;
import dblab.sharing_platform.dto.member.PasswordResetRequest;
import dblab.sharing_platform.exception.auth.DuplicateNicknameException;
import dblab.sharing_platform.exception.auth.DuplicateUsernameException;
import dblab.sharing_platform.exception.auth.EmailAuthNotEqualsException;
import dblab.sharing_platform.exception.auth.EmailAuthNotFoundException;
import dblab.sharing_platform.exception.auth.LoginFailureException;
import dblab.sharing_platform.exception.auth.NotEqualsPasswordToVerifiedException;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.exception.role.RoleNotFoundException;
import dblab.sharing_platform.repository.emailAuth.EmailAuthRepository;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.refresh.RefreshTokenRepository;
import dblab.sharing_platform.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static dblab.sharing_platform.domain.refresh.RefreshToken.createToken;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService {

    public static final String AUTH_KEY_SIGN_UP = "SIGN-UP";
    public static final String AUTH_KEY_RESET_PASSWORD = "RESET-PASSWORD";
    public static final String NONE = "None";

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final TokenProvider tokenProvider;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository tokenRepository;

    @Transactional
    public void signUp(MemberCreateRequest request){
        validateDuplicateUsernameAndNickname(request);
        validateEmailAuthKey(request, AUTH_KEY_SIGN_UP);
        List<Role> roles = List.of(roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(RoleNotFoundException::new));

        Member member = new Member(request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getPhoneNumber(),
                request.getAddress(),
                NONE,
                roles);

        memberRepository.save(member);
        emailAuthRepository.deleteByEmail(request.getUsername());
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(MemberNotFoundException::new);

        validateEmailAuthKey(request, AUTH_KEY_RESET_PASSWORD);
        validatePasswordEqualsVerifyPassword(request);

        member.updatePassword(passwordEncoder.encode(request.getPassword()));

        emailAuthRepository.deleteByEmail(request.getUsername());
    }

    @Transactional
    public void oAuth2Signup(OAuth2MemberCreateRequest request) {
        List<Role> roles = List.of(roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(RoleNotFoundException::new));

        Member member = new Member(request.getEmail(),
                passwordEncoder.encode(request.getEmail()),
                null,
                null,
                null,
                request.getProvider(),
                roles);
        memberRepository.save(member);
    }

    @Transactional
    public LogInResponse oauth2Login(OAuth2MemberCreateRequest request) {
        List<String> tokens = jwtLoginRequest(new LoginRequest(request.getEmail(), request.getEmail()));
        String accessToken = tokens.get(0);
        String refreshToken = tokens.get(1);
        return LogInResponse.toDto(accessToken, refreshToken);
    }

    @Transactional
    public LogInResponse login(LoginRequest request) {
        memberRepository.findByUsername(request.getUsername())
                .orElseThrow(MemberNotFoundException::new);

        List<String> tokens = jwtLoginRequest(request);
        String accessToken = tokens.get(0);
        String refreshToken = tokens.get(1);
        return LogInResponse.toDto(accessToken, refreshToken);
    }

    @Transactional
    public List<String> jwtLoginRequest(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String accessToken = tokenProvider.createToken(authentication, TokenProvider.ACCESS);
            String refreshToken = tokenProvider.createToken(authentication, TokenProvider.REFRESH);

            RefreshToken findToken = tokenRepository.findByUsername(request.getUsername())
                    .orElse(null);

            if (findToken == null) {
                tokenRepository.save(createToken(request.getUsername(), refreshToken));
            } else {
                findToken.changeToken(refreshToken);
            }

            List<String> tokens = new ArrayList<>();
            tokens.add(accessToken);
            tokens.add(refreshToken);

            return tokens;
        } catch (BadCredentialsException e) {
            throw new LoginFailureException();
        }
    }

    private void validateDuplicateUsernameAndNickname(MemberCreateRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException();
        } else if (memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException();
        }
    }

    private void validateEmailAuthKey(EmailAuthRequest request, String purpose) {
        EmailAuth emailAuth = emailAuthRepository.findByEmailAndPurpose(request.getUsername(), purpose)
                .orElseThrow(EmailAuthNotFoundException::new);
        if (!emailAuth.getKey().equals(request.getAuthKey())) {
            throw new EmailAuthNotEqualsException();
        }
    }
    private void validatePasswordEqualsVerifyPassword(PasswordResetRequest request) {
        if (!request.getPassword().equals(request.getVerifyPassword())) {
            throw new NotEqualsPasswordToVerifiedException();
        }
    }
}
