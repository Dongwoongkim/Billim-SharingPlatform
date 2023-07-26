package dblab.sharing_flatform.service.member;

import dblab.sharing_flatform.config.security.jwt.support.TokenProvider;
import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.role.RoleType;
import dblab.sharing_flatform.dto.member.crud.create.OAuth2MemberCreateRequestDto.OAuth2MemberCreateRequestDto;
import dblab.sharing_flatform.dto.member.login.LogInResponseDto;
import dblab.sharing_flatform.dto.member.login.LoginRequestDto;
import dblab.sharing_flatform.dto.member.crud.create.MemberCreateRequestDto;
import dblab.sharing_flatform.exception.auth.LoginFailureException;
import dblab.sharing_flatform.exception.member.DuplicateNicknameException;
import dblab.sharing_flatform.exception.member.DuplicateUsernameException;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.exception.role.RoleNotFoundException;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.role.RoleRepository;
import dblab.sharing_flatform.config.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public void signUp(MemberCreateRequestDto requestDto){
        validateDuplicateUsernameAndNickname(requestDto);

        Member member = new Member(requestDto.getUsername(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getNickname(),
                requestDto.getPhoneNumber(),
                requestDto.getAddress(),
                "None",
                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)));

        memberRepository.save(member);
    }

    @Transactional
    public void oAuth2Signup(OAuth2MemberCreateRequestDto requestDto) {
        Optional<Member> search = memberRepository.findByUsername(requestDto.getEmail());

        // DB에 없으면 회원가입
        if (search.isEmpty()) {
            Member member = new Member(requestDto.getEmail(),
                    passwordEncoder.encode(requestDto.getEmail()),
                    null,
                    null,
                    null,
                    requestDto.getProvider(),
                    List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)));
            memberRepository.save(member);
        }
    }

    public LogInResponseDto oauth2Login(OAuth2MemberCreateRequestDto requestDto) {
        String jwt = jwtLoginRequest(new LoginRequestDto(requestDto.getEmail(), requestDto.getEmail()));
        return LogInResponseDto.toDto(jwt);
    }

    private void validateDuplicateUsernameAndNickname(MemberCreateRequestDto requestDto) {
        if (memberRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateUsernameException();
        } else if (memberRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
    }

    public LogInResponseDto login(LoginRequestDto requestDto) {
        Member member = memberRepository.findByUsername(requestDto.getUsername()).orElseThrow(MemberNotFoundException::new);

        if (member != null) {
            String jwt = jwtLoginRequest(requestDto);
            return LogInResponseDto.toDto(jwt);
        }
        throw new MemberNotFoundException();
    }

    private String jwtLoginRequest(LoginRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String jwt = tokenProvider.createAccessToken(authentication);
            if (!StringUtils.hasText(jwt)) {
                throw new LoginFailureException();
            }
            return jwt;
        } catch (BadCredentialsException e) {
            throw new LoginFailureException();
        }
    }

    public Optional<Member> getMemberWithAuthorities(String username){
        return memberRepository.findOneWithRolesByUsername(username);
    }

    public Optional<Member> getMyMemberWithAuthorities(){
        return SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithRolesByUsername);
    }

}
