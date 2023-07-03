package dblab.sharing_flatform.repository.member;

import dblab.sharing_flatform.domain.member.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<Member> findOneWithRolesByUsername(String username);

    boolean existsByUsername(String username);
}
