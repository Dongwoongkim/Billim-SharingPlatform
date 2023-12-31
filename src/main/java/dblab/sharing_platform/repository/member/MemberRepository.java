package dblab.sharing_platform.repository.member;

import dblab.sharing_platform.domain.member.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QMemberRepository{

    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    @EntityGraph(attributePaths = "roles")
    Optional<Member> findOneWithRolesByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndProvider(String username, String provider);

    boolean existsByNickname(String nickname);

    Optional<Member> findByProviderAndUsername(String provider, String username);
}
