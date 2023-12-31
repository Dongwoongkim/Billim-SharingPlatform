package dblab.sharing_platform.domain.emailAuth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth {

    @Id
    @Column(name = "email_key", nullable = false)
    private String key;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String purpose;

    public EmailAuth(String key, String email, String purpose) {
        this.key = key;
        this.email = email;
        this.purpose = purpose;
    }
}
