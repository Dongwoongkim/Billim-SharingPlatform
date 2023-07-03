package dblab.sharing_flatform.domain.member;

import dblab.sharing_flatform.domain.role.Role;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MemberRoleId implements Serializable {
    private Member member;
    private Role role;
}