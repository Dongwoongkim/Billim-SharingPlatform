package dblab.sharing_platform.dto.member;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.role.RoleType;
import dblab.sharing_platform.dto.image.ProfileImageDto;
import dblab.sharing_platform.dto.post.PostDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileDto {

    private ProfileImageDto profileImage;
    private String nickname;
    private String introduce;
    private RoleType memberRole;
    private List<PostDto> posts;

    public static MemberProfileDto toDto(Member member, List<PostDto> postDtoList) {
        return new MemberProfileDto(
                ProfileImageDto.toDto(member.getProfileImage()),
                member.getNickname(),
                member.getIntroduce(),
                member.getRoles().get(0).getRole().getRoleType(),
                postDtoList);
    }
}