package dblab.sharing_flatform.service.member;

import dblab.sharing_flatform.config.security.jwt.support.TokenProvider;
import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.role.RoleType;
import dblab.sharing_flatform.dto.member.LogInResponseDto;
import dblab.sharing_flatform.dto.member.LoginRequestDto;
import dblab.sharing_flatform.dto.member.SignUpRequestDto;
import dblab.sharing_flatform.exception.auth.LoginFailureException;
import dblab.sharing_flatform.exception.member.DuplicateUsernameException;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.exception.role.RoleNotFoundException;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.role.RoleRepository;
import dblab.sharing_flatform.util.SecurityUtil;

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
    public void signUp(SignUpRequestDto signUpRequestDto){

        validateDuplicateUsername(signUpRequestDto);

        Member member = new Member(signUpRequestDto.getUsername(),
                passwordEncoder.encode(signUpRequestDto.getPassword()),
                signUpRequestDto.getPhoneNumber(),
                signUpRequestDto.getAddress(),
                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)),
                List.of());
        memberRepository.save(member);
    }

    private void validateDuplicateUsername(SignUpRequestDto signUpRequestDto) {
        if (memberRepository.findOneWithRolesByUsername(signUpRequestDto.getUsername()).orElse(null) != null) {
            throw new DuplicateUsernameException();
        }
    }

    public LogInResponseDto login(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(MemberNotFoundException::new);

        if (member != null) {
            String jwt = jwtLoginRequest(loginRequestDto);
            return LogInResponseDto.toDto(jwt);
        }
        throw new MemberNotFoundException();
    }

    private String jwtLoginRequest(LoginRequestDto loginRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String jwt = tokenProvider.createAccessToken(authentication);
            log.info("jwt = {} ", jwt);
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
