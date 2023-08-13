package dblab.sharing_platform.service.member;


import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.dto.member.*;
import dblab.sharing_platform.dto.post.PostDto;
import dblab.sharing_platform.exception.auth.DuplicateNicknameException;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import dblab.sharing_platform.service.file.MemberFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberFileService postFileService;

    public MemberPrivateDto readCurrentUserInfoByUsername(String username){
        return MemberPrivateDto.toDto(memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new));
    }

    public MemberProfileDto readMemberProfileByNickname(String nickname) {
        List<Post> posts = postRepository.findAllWithMemberByNickname(nickname);
        return MemberProfileDto.toDto(memberRepository.findByNickname(nickname).orElseThrow(MemberNotFoundException::new), PostDto.toDtoList(posts));
    }

    public PagedMemberListDto readAllMemberByCond(MemberPagingCondition cond) {
        return PagedMemberListDto.toDto(memberRepository.findAllBySearch(cond));
    }

    @Transactional
    public void deleteMemberByUsername(String username){
        memberRepository.delete(memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new));
    }

    @Transactional
    public MemberPrivateDto updateMember(String username, MemberUpdateRequestDto requestDto){
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);
        memberUpdate(requestDto, member);
        return MemberPrivateDto.toDto(member);
    }

    @Transactional
    public MemberPrivateDto oauthMemberUpdate(String username, OAuthMemberUpdateRequestDto requestDto){
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);
        oAuthMemberUpdate(requestDto, member);
        return MemberPrivateDto.toDto(member);
    }

    private String encodeRawPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void memberUpdate(MemberUpdateRequestDto requestDto, Member member) {
        validateDuplicateNickname(requestDto.getNickname());
        if (requestDto.getImage() != null) {
            member.updateMember(requestDto);
            String profileImage = member.updateMemberProfileImage(requestDto);
            profileImageServerUpdate(requestDto.getImage(), member, profileImage);
        } else {
            member.updateMember(requestDto);
        }
    }

    private void oAuthMemberUpdate(OAuthMemberUpdateRequestDto requestDto, Member member) {
        validateDuplicateNickname(requestDto.getNickname());
        String profileImage = member.updateOAuthMember(requestDto);
        profileImageServerUpdate(requestDto.getImage(), member, profileImage);
    }

    private void profileImageServerUpdate(MultipartFile file, Member member, String profileImage) {
        if (file != null) {
            postFileService.upload(file, member.getProfileImage().getUniqueName());
        }
        if (profileImage != null) {
            postFileService.delete(profileImage);
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }
    }

}