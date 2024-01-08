package dblab.sharing_platform.service.oauth;

import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.ACCESS_TOKEN_URL_GOOGLE;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.ACCESS_TOKEN_URL_KAKAO;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.ACCESS_TOKEN_URL_NAVER;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.CLIENT_ID;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.CLIENT_SECRET;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.CODE;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.GOOGLE;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.GRANT_TYPE;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.KAKAO;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.NAVER;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.REDIRECT_URI;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.UNLINK_URL_GOOGLE;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.UNLINK_URL_KAKAO;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.UNLINK_URL_NAVER_END;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.UNLINK_URL_NAVER_FRONT;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.USER_INFO_URL_GOOGLE;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.USER_INFO_URL_KAKAO;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.USER_INFO_URL_NAVER;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dblab.sharing_platform.dto.member.OAuth2MemberCreateRequest;
import dblab.sharing_platform.dto.oauth.AccessTokenRequest;
import dblab.sharing_platform.dto.oauth.GoogleProfile;
import dblab.sharing_platform.dto.oauth.KakaoProfile;
import dblab.sharing_platform.dto.oauth.NaverProfile;
import dblab.sharing_platform.exception.auth.AlreadyExistsMemberException;
import dblab.sharing_platform.exception.oauth.OAuthCommunicationException;
import dblab.sharing_platform.exception.oauth.OAuthUserNotFoundException;
import dblab.sharing_platform.exception.oauth.SocialAgreementException;
import dblab.sharing_platform.repository.member.MemberRepository;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private static final String NONE = "None";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String POST = "POST";
    private static final String GET = "GET";

    private final Gson gson;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;

    public OAuth2MemberCreateRequest getAccessToken(String code, String provider,
                                                    AccessTokenRequest accessTokenRequest) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "";

        switch (provider) {
            case KAKAO:
                reqURL = ACCESS_TOKEN_URL_KAKAO;
                break;
            case GOOGLE:
                reqURL = ACCESS_TOKEN_URL_GOOGLE;
                break;
            case NAVER:
                reqURL = ACCESS_TOKEN_URL_NAVER;
                break;
        }

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(POST);
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append(GRANT_TYPE);
            sb.append(CLIENT_ID + accessTokenRequest.getClientId());
            sb.append(CLIENT_SECRET + accessTokenRequest.getClientSecret());
            sb.append(REDIRECT_URI + accessTokenRequest.getRedirectUri());
            sb.append(CODE + code);
            bw.write(sb.toString());
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonElement element = JsonParser.parseString(result);
            access_Token = element.getAsJsonObject().get(ACCESS_TOKEN).getAsString();

            br.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return getProvideInfo(provider, access_Token);
    }

    public Object getOAuthUserInfo(String accessToken, String provider) {

        String reqURL = "";

        switch (provider) {
            case KAKAO:
                reqURL = USER_INFO_URL_KAKAO;
                break;
            case GOOGLE:
                reqURL = USER_INFO_URL_GOOGLE;
                break;
            case NAVER:
                reqURL = USER_INFO_URL_NAVER;
                break;
        }

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(GET);
            conn.setDoOutput(true);
            conn.setRequestProperty(AUTHORIZATION, BEARER + accessToken);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            if (provider == KAKAO) {
                KakaoProfile kakaoProfile = gson.fromJson(result, KakaoProfile.class);
                br.close();
                return kakaoProfile;
            } else if (provider == GOOGLE) {
                GoogleProfile googleProfile = gson.fromJson(result, GoogleProfile.class);
                br.close();
                return googleProfile;
            } else {
                NaverProfile naverProfile = gson.fromJson(result, NaverProfile.class);
                br.close();
                return naverProfile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unlinkOAuthService(String accessToken, String provider) {
        String url = "";
        switch (provider) {
            case KAKAO:
                url = UNLINK_URL_KAKAO;
                break;
            case NAVER:
                url = UNLINK_URL_NAVER_FRONT + accessToken.replaceAll("'", "") + UNLINK_URL_NAVER_END;
                break;
            case GOOGLE:
                url = UNLINK_URL_GOOGLE + accessToken;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(AUTHORIZATION, BEARER + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return;
        }
        throw new OAuthCommunicationException();
    }

    private OAuth2MemberCreateRequest getProvideInfo(String provider, String accessToken) {
        String email = "";
        switch (provider) {
            case KAKAO:
                KakaoProfile oAuthKakaoInfo = (KakaoProfile) getOAuthUserInfo(accessToken, provider);
                if (oAuthKakaoInfo == null) {
                    throw new OAuthUserNotFoundException();
                }
                email = oAuthKakaoInfo.getKakao_account().getEmail();
                break;
            case GOOGLE:
                GoogleProfile oAuthGoogleInfo = (GoogleProfile) getOAuthUserInfo(accessToken, provider);
                if (oAuthGoogleInfo == null) {
                    throw new OAuthUserNotFoundException();
                }
                email = oAuthGoogleInfo.getEmail();
                break;
            case NAVER:
                NaverProfile oAuthNaverInfo = (NaverProfile) getOAuthUserInfo(accessToken, provider);
                if (oAuthNaverInfo == null) {
                    throw new OAuthUserNotFoundException();
                }
                email = oAuthNaverInfo.getResponse().getEmail();
                break;
        }

        if (email == null) {
            unlinkOAuthService(accessToken, provider);
            throw new SocialAgreementException();
        } else if (memberRepository.existsByUsernameAndProvider(email, NONE)) {
            throw new AlreadyExistsMemberException();
        }
        return new OAuth2MemberCreateRequest(email, provider, accessToken);
    }
}
