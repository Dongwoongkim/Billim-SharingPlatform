package dblab.sharing_platform.config.oauth;

import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.GOOGLE;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.KAKAO;
import static dblab.sharing_platform.config.oauth.provider.OAuthInfo.NAVER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dblab.sharing_platform.config.oauth.provider.GoogleUserInfo;
import dblab.sharing_platform.config.oauth.provider.KakaoUserInfo;
import dblab.sharing_platform.config.oauth.provider.NaverUserInfo;
import dblab.sharing_platform.config.oauth.provider.OAuth2UserInfo;
import dblab.sharing_platform.config.security.details.MemberDetails;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.role.RoleType;
import dblab.sharing_platform.exception.role.RoleNotFoundException;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.role.RoleRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private static final String RESPONSE = "response";

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        System.out.println("DID1");

        switch (registrationId) {
            case KAKAO:
                oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
                break;
            case NAVER:
                ObjectMapper objectMapper = new ObjectMapper();
                Object naverAccount = oAuth2User.getAttributes().get(RESPONSE);
                Map<String, Object> account = objectMapper.convertValue(naverAccount,
                        new TypeReference<Map<String, Object>>() {
                        });
                oAuth2UserInfo = new NaverUserInfo(account);
                break;
            case GOOGLE:
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }
        Optional<Member> memberEntity = memberRepository.findByProviderAndUsername(oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getEmail());
        Member member = saveOrUpdate(oAuth2UserInfo, memberEntity);
        
        return new MemberDetails(Long.toString(member.getId()), member.getUsername(), null, List.of(),
                oAuth2User.getAttributes());
    }

    public Member saveOrUpdate(OAuth2UserInfo oAuth2UserInfo, Optional<Member> memberEntity) {
        Member member;
        if (memberEntity.isPresent()) {
            member = memberEntity.get();
        } else {
            member = new Member(oAuth2UserInfo.getEmail(),
                    "",
                    null,
                    oAuth2UserInfo.getPhoneNumber(),
                    oAuth2UserInfo.getAddress(),
                    oAuth2UserInfo.getProvider(),
                    List.of(roleRepository.findByRoleType(RoleType.USER)
                            .orElseThrow(RoleNotFoundException::new)));
            memberRepository.save(member);
        }
        return member;
    }
}
