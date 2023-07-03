package dblab.sharing_flatform.repository.member;

import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.member.MemberRole;
import dblab.sharing_flatform.domain.role.Role;
import dblab.sharing_flatform.domain.role.RoleType;
import dblab.sharing_flatform.exception.role.RoleNotFoundException;
import dblab.sharing_flatform.repository.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static dblab.sharing_flatform.factory.member.MemberFactory.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void findByUsernameTest(){
        // given
        Member member = createMember();
        memberRepository.save(member);
        clear();

        // when
        Member findMember = memberRepository.findByUsername(member.getUsername()).get();

        // then
        assertThat(findMember.getUsername()).isEqualTo("username");
    }

    @Test
    public void findOneWithRolesByUsernameTest(){

        // given
        List<RoleType> roleTypes = List.of(RoleType.ADMIN, RoleType.MANAGER, RoleType.USER);
        List<Role> roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(toList());
        roleRepository.saveAll(roles);
        clear();

        // given
        Member member = new Member("user", "password", "phoneNum", createAddress(),
                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)), List.of());
        memberRepository.save(member);
        clear();

        // then
        Member findMember = memberRepository.findOneWithRolesByUsername(member.getUsername()).get();
        List<MemberRole> memberRoles = findMember.getRoles();
        memberRoles.stream().map(mr -> assertThat(mr.getRole().getRoleType()).isEqualTo(RoleType.USER));
    }


    @Test
    public void cascadeCreateMemberToMemberRoleTest(){
        // given
        List<RoleType> roleTypes = List.of(RoleType.ADMIN, RoleType.MANAGER, RoleType.USER);
        List<Role> roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(toList());
        roleRepository.saveAll(roles);
        clear();

        // when
        Member member = new Member("user", "password", "phoneNum", createAddress(),
                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)), List.of());
        memberRepository.save(member);
        clear();

        List<MemberRole> result = em.createQuery("select mr from MemberRole mr", MemberRole.class).getResultList();

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void cascadeDeleteMemberToMemberRoleTest(){
        // given
        List<RoleType> roleTypes = List.of(RoleType.ADMIN, RoleType.MANAGER, RoleType.USER);
        List<Role> roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(toList());
        roleRepository.saveAll(roles);
        clear();

        Member member = new Member("user", "password", "phoneNum", createAddress(),
                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new)), List.of());
        memberRepository.save(member);
        clear();

        // when
        memberRepository.deleteById(member.getId());
        clear();

        List<MemberRole> result = em.createQuery("select mr from MemberRole mr", MemberRole.class).getResultList();

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void existsByUsernameTest() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        clear();

        // then
        assertThat(memberRepository.existsByUsername(member.getUsername())).isTrue();
    }


    private void clear() {
        em.flush();
        em.clear();
    }

}
