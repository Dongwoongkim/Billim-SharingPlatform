package dblab.sharing_flatform.domain.member;

import dblab.sharing_flatform.domain.address.Address;
import dblab.sharing_flatform.factory.MemberFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static dblab.sharing_flatform.factory.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    public void createMemberTest() throws Exception {
        //given
        Member member = createMember();

        //then
        assertThat(member).isNotNull();
    }

    @Test
    public void updateMemberTest() throws Exception {
        //given
        Member member = createMember();
        String phoneNumber = "TestNumber";
        String password = "TestPassword";
        Address address = new Address("TestCity", "TestDistrict", "TestStreet", "TestZipcode");

        //when
        member.updateUserInfo(password, phoneNumber, address);

        //then
        assertThat(member.getPassword()).isEqualTo(password);
        assertThat(member.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(member.getAddress()).isEqualTo(address);

    }
    
    @Test  
    public void initPostsTest() throws Exception {
        Member member = createMember();

    }
}