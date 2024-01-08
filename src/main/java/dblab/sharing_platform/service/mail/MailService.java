package dblab.sharing_platform.service.mail;


import static dblab.sharing_platform.config.mail.MailConfigInfo.HOST_EMAIL;

import dblab.sharing_platform.domain.emailAuth.EmailAuth;
import dblab.sharing_platform.exception.auth.AlreadySendAuthKeyException;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.repository.emailAuth.EmailAuthRepository;
import dblab.sharing_platform.repository.member.MemberRepository;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MailService {

    private static final String SIGN_UP = "SIGN-UP";
    private static final String RESET_PASSWORD = "RESET-PASSWORD";
    private static final String CHARSET = "utf-8";
    private static final String SUBTYPE = "html";
    private static final String PERSONAL = "SharingPlatform_Admin";
    private String authKey;

    private final JavaMailSender emailSender;
    private final EmailAuthRepository emailAuthRepository;
    private final MemberRepository memberRepository;
    
    @Transactional
    public void sendSignUpMail(String email) {
        if (emailAuthRepository.existsByEmailAndPurpose(email, SIGN_UP)) {
            throw new AlreadySendAuthKeyException();
        }
        authKey = createAuthKey();
        sendMail(email, SIGN_UP);
    }

    @Transactional
    public void sendResetPasswordMail(String email) {
        if (emailAuthRepository.existsByEmailAndPurpose(email, RESET_PASSWORD)) {
            throw new AlreadySendAuthKeyException();
        }
        authKey = createAuthKey();
        memberRepository.findByUsername(email)
                .orElseThrow(MemberNotFoundException::new);
        sendMail(email, RESET_PASSWORD);
    }

    public MimeMessage creatMessage(String email, String purpose)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = setMessageContent(email, purpose);
        return message;
    }

    public String createAuthKey() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        String key = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return key;
    }

    private void sendMail(String email, String purpose) {
        MimeMessage message = null;

        try {
            message = creatMessage(email, purpose);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            emailSender.send(message);
            emailAuthRepository.save(new EmailAuth(authKey, email, purpose));
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private MimeMessage setMessageContent(String email, String purpose)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        String msgg = "";

        switch (purpose) {
            case "RESET-PASSWORD":
                message.setSubject("[Billim]" + "Billim 비밀번호 재설정 페이지에 대한 인증코드 입니다.");
                msgg += "<h1>안녕하세요</h1>";
                msgg += "<h1>대여 서비스 플랫폼 Billim 입니다</h1>";
                msgg += "<br>";
                msgg += "<p>아래 인증코드를 비밀번호 재설정 페이지에 입력해주세요</p>";
                msgg += "<br>";
                msgg += "<br>";
                msgg += "<div align='center' style='border:1px solid black'>";
                msgg += "<h3 style='color:blue'>비밀번호 재설정을 하기 위한 사전인증코드 입니다</h3>";
                msgg += "<div style='font-size:130%'>";
                msgg += "<strong>" + authKey + "</strong></div><br/>";
                msgg += "</div>";
                break;

            case "SIGN-UP":
                message.setSubject("[Billim]" + "Billim 회원가입 인증코드 입니다.");
                msgg += "<h1>안녕하세요</h1>";
                msgg += "<h1>대여 서비스 플랫폼 Billim 입니다</h1>";
                msgg += "<br>";
                msgg += "<p>아래 인증코드를 회원가입 페이지에 입력해주세요</p>";
                msgg += "<br>";
                msgg += "<br>";
                msgg += "<div align='center' style='border:1px solid black'>";
                msgg += "<h3 style='color:blue'>회원가입 인증코드 입니다</h3>";
                msgg += "<div style='font-size:130%'>";
                msgg += "<strong>" + authKey + "</strong></div><br/>";
                msgg += "</div>";
                break;
        }
        message.setText(msgg, CHARSET, SUBTYPE);
        message.setFrom(new InternetAddress(HOST_EMAIL, PERSONAL));

        return message;
    }
}
