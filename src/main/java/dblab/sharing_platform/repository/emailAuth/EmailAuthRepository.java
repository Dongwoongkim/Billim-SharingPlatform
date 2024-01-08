package dblab.sharing_platform.repository.emailAuth;

import dblab.sharing_platform.domain.emailAuth.EmailAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, String> {

    Optional<EmailAuth> findByEmailAndPurpose(String email, String purpose);

    boolean existsByEmailAndPurpose(String email, String purpose);

    void deleteByEmail(String email);
}
