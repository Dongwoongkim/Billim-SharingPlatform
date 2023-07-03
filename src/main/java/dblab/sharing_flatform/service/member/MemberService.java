package dblab.sharing_flatform.service.member;


import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.dto.member.MemberResponseDto;
import dblab.sharing_flatform.dto.member.MemberUpdateRequestDto;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponseDto getMemberInfo(String username){
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);
        return MemberResponseDto.toDto(member);
    }

    @Transactional
    public void delete(Long id){
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);
    }

    @Transactional
    public MemberResponseDto update(Long id, MemberUpdateRequestDto memberUpdateRequestDto){
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        member.updateMember(memberUpdateRequestDto);

        return MemberResponseDto.toDto(member);
    }
}
