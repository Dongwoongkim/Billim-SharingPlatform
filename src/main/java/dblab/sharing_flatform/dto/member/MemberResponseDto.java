package dblab.sharing_flatform.dto.member;

import dblab.sharing_flatform.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {

    private Long id;
    private String username;
    private String password;
    private String phoneNum;

    public static MemberResponseDto toDto(Member member) {
        return new MemberResponseDto(
                member.getId(),
                member.getUsername(),
                member.getPassword(),
                member.getPhoneNumber());
    }
}