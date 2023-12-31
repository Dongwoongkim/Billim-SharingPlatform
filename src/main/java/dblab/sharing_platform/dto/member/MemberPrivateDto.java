package dblab.sharing_platform.dto.member;

import dblab.sharing_platform.domain.embedded.address.Address;
import dblab.sharing_platform.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberPrivateDto {

    private Long id;
    private String username;
    private String nickname;
    private String phoneNumber;
    private Address address;

    public static MemberPrivateDto toDto(Member member) {
        return new MemberPrivateDto(
                member.getId(),
                member.getUsername(),
                member.getNickname(),
                member.getPhoneNumber(),
                member.getAddress());
    }
}