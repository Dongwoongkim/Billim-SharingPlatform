package dblab.sharing_flatform.config.security.detailsService;

import dblab.sharing_flatform.config.security.details.MemberDetails;
import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.member.MemberRole;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // authenticate() 메소드 실행 시 넘어온 UsernamePasswordAuthentication 내부에
    // username, password 추출
    // username ->  role까지 graph로 조회
    // password ->  DaoAuthenticationProvider가 Encoding된 Password와 DB의 password 자동 비교
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = Optional.ofNullable(memberRepository.findOneWithRolesByUsername(username).
                orElseThrow(MemberNotFoundException::new));

        return createUser(member.get());
    }

    private UserDetails createUser(Member member) {
        List<MemberRole> roles = member.getRoles();
        List<SimpleGrantedAuthority> authorities = roles.stream().
                map(mr -> new SimpleGrantedAuthority(mr.getRole().getRoleType().toString())).collect(Collectors.toList());

        return new MemberDetails(String.valueOf(member.getId()), member.getUsername(), member.getPassword(), authorities);
    }
}
