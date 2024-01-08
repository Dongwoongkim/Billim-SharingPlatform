package dblab.sharing_platform.repository.refresh;

import dblab.sharing_platform.domain.refresh.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUsername(String username);

    Optional<RefreshToken> findByToken(String token);
}
