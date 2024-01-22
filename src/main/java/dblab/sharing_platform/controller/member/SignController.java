package dblab.sharing_platform.controller.member;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import dblab.sharing_platform.dto.member.LogInResponse;
import dblab.sharing_platform.dto.member.LoginRequest;
import dblab.sharing_platform.dto.member.MemberCreateRequest;
import dblab.sharing_platform.dto.member.OAuth2MemberCreateRequest;
import dblab.sharing_platform.dto.member.PasswordResetRequest;
import dblab.sharing_platform.dto.oauth.AccessTokenRequest;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.service.mail.MailService;
import dblab.sharing_platform.service.member.MemberService;
import dblab.sharing_platform.service.member.SignService;
import dblab.sharing_platform.service.oauth.OAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Sign Controller", tags = "Sign")
@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;
    private final MemberService memberService;
    private final OAuthService oAuthService;
    private final MailService mailService;

    @ApiOperation(value = "일반 회원가입", notes = "일반 회원가입을 한다.")
    @PostMapping("/auth/sign-up")
    public ResponseEntity signUp(@Valid @RequestBody MemberCreateRequest memberCreateRequest) {
        signService.signUp(memberCreateRequest);
        return new ResponseEntity(CREATED);
    }

    @ApiOperation(value = "일반 로그인", notes = "일반 로그인을 한다.")
    @PostMapping("/auth/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity(signService.login(loginRequest), OK);
    }

    @ApiOperation(value = "비밀번호 재설정", notes = "비밀번호를 재설정합니다.")
    @PostMapping("/password-reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        signService.resetPassword(passwordResetRequest);
        return new ResponseEntity(OK);
    }

    @ApiOperation(value = "회원가입을 위한 이메일 인증", notes = "회원가입에서 이메일 인증을 위한 엔드포인트")
    @PostMapping("/email/sign-up")
    public ResponseEntity signUpMailConfirm(@RequestParam(name = "email") String email) {
        mailService.sendSignUpMail(email);
        return new ResponseEntity(OK);
    }

    @ApiOperation(value = "비밀번호 재설정을 위한 이메일 인증", notes = "비밀번호 재설정 페이지에서 이메일 인증을 위한 엔드포인트")
    @PostMapping("/email/password-reset")
    public ResponseEntity resetPasswordMailConfirm(@RequestParam(name = "email") String email) {
        mailService.sendResetPasswordMail(email);
        return new ResponseEntity(OK);
    }

    @ApiOperation(value = "카카오 로그인", notes = "OAuth2.0 카카오로 소셜 로그인을 진행합니다.")
    @GetMapping("/oauth2/callback/kakao")
    public ResponseEntity oauth2Login(@RequestParam String code,
                                      @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String clientId,
                                      @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") String clientSecret,
                                      @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") String redirectUri) {
        
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest(clientId, clientSecret, redirectUri);
        OAuth2MemberCreateRequest req = oAuthService.getAccessToken(code, "kakao", accessTokenRequest);

        return new ResponseEntity(signUpAndLogin(req), OK);
    }

    @ApiOperation(value = "구글 로그인", notes = "OAuth2.0 구글로 소셜 로그인을 진행합니다.")
    @GetMapping("/oauth2/callback/google")
    public ResponseEntity oauth2LoginByGoogle(@RequestParam String code,
                                              @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
                                              @Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret,
                                              @Value("${spring.security.oauth2.client.registration.google.redirect-uri}") String redirectUri) {

        AccessTokenRequest accessTokenRequest = new AccessTokenRequest(clientId, clientSecret, redirectUri);
        OAuth2MemberCreateRequest req = oAuthService.getAccessToken(code, "google", accessTokenRequest);

        return new ResponseEntity(signUpAndLogin(req), OK);
    }

    @ApiOperation(value = "네이버 로그인", notes = "OAuth2.0 네이버로 소셜 로그인을 진행합니다.")
    @GetMapping("/oauth2/callback/naver")
    public ResponseEntity oauth2LoginByNaver(@RequestParam String code,
                                             @Value("${spring.security.oauth2.client.registration.naver.client-id}") String clientId,
                                             @Value("${spring.security.oauth2.client.registration.naver.client-secret}") String clientSecret,
                                             @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}") String redirectUri) {

        AccessTokenRequest accessTokenRequest = new AccessTokenRequest(clientId, clientSecret, redirectUri);
        OAuth2MemberCreateRequest req = oAuthService.getAccessToken(code, "naver", accessTokenRequest);

        return new ResponseEntity(signUpAndLogin(req), OK);
    }

    private LogInResponse signUpAndLogin(OAuth2MemberCreateRequest req) {
        try {
            memberService.readCurrentUserInfoByUsername(req.getEmail());
        } catch (MemberNotFoundException e) {
            signService.oAuth2Signup(req);
        } finally {
            return getLogInResponse(req);
        }
    }

    private LogInResponse getLogInResponse(OAuth2MemberCreateRequest req) {
        return signService.oauth2Login(req);
    }
}
