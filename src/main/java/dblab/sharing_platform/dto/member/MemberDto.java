package dblab.sharing_platform.dto.member;


import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.role.RoleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {

    private Long memberId;
    private String nickname;
    private RoleType memberRoles;

    public static MemberDto toDto(Member member) {
        return new MemberDto(member.getId(), member.getNickname(), member.getRoles().get(0).getRole().getRoleType());
    }
}
