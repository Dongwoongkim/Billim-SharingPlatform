package dblab.sharing_flatform.service.member;

import dblab.sharing_flatform.domain.address.Address;
import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.dto.member.MemberResponseDto;
import dblab.sharing_flatform.dto.member.MemberUpdateRequestDto;
import dblab.sharing_flatform.repository.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static dblab.sharing_flatform.factory.member.MemberFactory.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void clear() {
        memberRepository.deleteAll();
    }


    // getMemberInfo
    @Test
    public void getMemberInfoTest() throws Exception {
        //given
        Member member = createMember();
        memberRepository.save(member);

        //when
        MemberResponseDto memberInfo = memberService.getMemberInfo(member.getUsername());

        //then
        assertThat(memberInfo.getUsername()).isEqualTo(member.getUsername());
    }

    // delete
    @Test
    public void deleteMemberTest() throws Exception {

        //given
        Member member = createMember();
        memberRepository.save(member);

        //when
        memberService.delete(member.getId());

        //then
        assertThat(memberRepository.count()).isEqualTo(0);
    }


    // update
    @Test
    public void updateMemberTest() throws Exception{

        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Address address = new Address("TestCity", "TestDistrict", "TestStreet", "TestZipcode");
        MemberUpdateRequestDto memberUpdateRequestDto = new MemberUpdateRequestDto("updatePass1!", "01011112222", address);
        MemberResponseDto updateMember = memberService.update(member.getId(), memberUpdateRequestDto);

        // then
        assertThat(updateMember.getUsername()).isEqualTo("updateUser");
    }


}