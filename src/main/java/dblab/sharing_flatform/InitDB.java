package dblab.sharing_flatform;

import dblab.sharing_flatform.domain.address.Address;
import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.role.Role;
import dblab.sharing_flatform.domain.role.RoleType;
import dblab.sharing_flatform.exception.role.RoleNotFoundException;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void initDB() {
        initRoles();
        initAdmin();
        initManager();
        initMember();
    }

    @Transactional
    public void initRoles() {
        RoleType[] values = RoleType.values();
        List<Role> roles = Arrays.stream(values).map(roleType -> new Role(roleType)).collect(Collectors.toList());
        roleRepository.saveAll(roles);
    }

    @Transactional
    public void initAdmin() {
        memberRepository.save(new Member(
                "admin", passwordEncoder.encode("1q2w3e4r@@"),
                "011", new Address("jeonju", "d1", "s1", "z1"),
                List.of(roleRepository.findByRoleType(RoleType.ADMIN).orElseThrow(RoleNotFoundException::new),
                        roleRepository.findByRoleType(RoleType.MANAGER).orElseThrow(RoleNotFoundException::new),
                        roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)
                ),
                List.of()
        ));
    }

    @Transactional
    public void initManager() {
        memberRepository.save(new Member(
                "manager", passwordEncoder.encode("2w2w2w3e!"),
                "012", new Address("jeonju", "d1", "s1", "z1"),
                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new),
                        roleRepository.findByRoleType(RoleType.MANAGER).orElseThrow(RoleNotFoundException::new)),
                List.of()
        ));
    }

    @Transactional
    public void initMember() {
        memberRepository.save(new Member(
                "member", passwordEncoder.encode("2w2w2w3e!"),
                "013", new Address("jeonju", "d1", "s1", "z1"),
                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)),
                List.of()
        ));
    }



}
