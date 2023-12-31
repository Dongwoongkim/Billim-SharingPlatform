package dblab.sharing_platform.domain.member;

import dblab.sharing_platform.domain.embedded.address.Address;
import dblab.sharing_platform.dto.member.MemberUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dblab.sharing_platform.factory.member.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    @DisplayName("회원 저장")
    public void createMemberTest() throws Exception {
        //given
        Member member = createMember();
        //then
        assertThat(member).isNotNull();
    }

    @Test
    @DisplayName("회원 정보 수정")
    public void updateMemberTest() throws Exception {
        //given
        Member member = createMember();

        String phoneNumber = "TestNumber";
        String password = "TestPassword";
        String nickname = "nickname";
        String introduce = "hi~ I'm test member!";
        Address address = new Address("TestCity", "TestDistrict", "TestStreet", "TestZipcode");

        //when
        member.updateMember(new MemberUpdateRequest(nickname, phoneNumber, address, introduce, null));

        //then
        assertThat(member.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(member.getAddress()).isEqualTo(address);
        assertThat(member.getIntroduce()).isEqualTo(introduce);
    }
}