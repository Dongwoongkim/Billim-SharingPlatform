package dblab.sharing_platform.config.oauth.provider;

import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.EMAIL;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.KAKAO;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.KAKAO_ACCOUNT;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.PROFILE_NICKNAME;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dblab.sharing_platform.domain.embedded.address.Address;
import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return KAKAO;
    }

    @Override
    public String getPhoneNumber() {
        return "";
    }

    @Override
    public String getEmail() {
        ObjectMapper objectMapper = new ObjectMapper();
        Object kakaoAccount = attributes.get(KAKAO_ACCOUNT);
        Map<String, Object> account = objectMapper.convertValue(kakaoAccount, new TypeReference<Map<String, Object>>() {
        });
        return (String) account.get(EMAIL);
    }

    @Override
    public String getName() {
        return (String) attributes.get(PROFILE_NICKNAME);
    }

    @Override
    public Address getAddress() {
        return new Address("", "", "", "");
    }
}
