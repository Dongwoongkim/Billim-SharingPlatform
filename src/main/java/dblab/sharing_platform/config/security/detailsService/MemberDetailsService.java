package dblab.sharing_platform.config.security.detailsService;

import dblab.sharing_platform.config.security.details.MemberDetails;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.member.MemberRole;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.repository.member.MemberRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = Optional.ofNullable(memberRepository.findOneWithRolesByUsername(username)
                .orElseThrow(MemberNotFoundException::new));
        return createUser(member.get());
    }

    private UserDetails createUser(Member member) {
        List<MemberRole> roles = member.getRoles();
        List<SimpleGrantedAuthority> authorities = roles.stream().
                map(mr -> new SimpleGrantedAuthority(mr.getRole().getRoleType().toString()))
                .collect(Collectors.toList());

        return new MemberDetails(String.valueOf(member.getId()), member.getUsername(), member.getPassword(),
                authorities, Map.of());
    }
}
