package dblab.sharing_platform.service.member;


import static dblab.sharing_platform.config.file.FileInfo.FOLDER_NAME_MEMBER_PROFILE;
import static dblab.sharing_platform.config.file.FileInfo.FOLDER_NAME_POST;

import dblab.sharing_platform.domain.image.PostImage;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.dto.member.MemberPagingCondition;
import dblab.sharing_platform.dto.member.MemberPrivateDto;
import dblab.sharing_platform.dto.member.MemberProfileDto;
import dblab.sharing_platform.dto.member.MemberUpdateRequest;
import dblab.sharing_platform.dto.member.OAuthMemberUpdateRequest;
import dblab.sharing_platform.dto.member.PagedMemberListDto;
import dblab.sharing_platform.dto.post.PostDto;
import dblab.sharing_platform.exception.auth.DuplicateNicknameException;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import dblab.sharing_platform.service.file.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FileService fileService;

    public MemberPrivateDto readCurrentUserInfoByUsername(String username) {
        return MemberPrivateDto.toDto(memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new));
    }

    public MemberProfileDto readMemberProfileByNickname(String nickname) {
        List<Post> posts = postRepository.findAllWithMemberByNickname(nickname);
        return MemberProfileDto.toDto(
                memberRepository.findByNickname(nickname)
                        .orElseThrow(MemberNotFoundException::new),
                PostDto.toDtoList(posts));
    }

    public PagedMemberListDto readAllMemberByCond(MemberPagingCondition cond) {
        return PagedMemberListDto.toDto(memberRepository.findAllBySearch(cond));
    }

    @Transactional
    public void deleteMemberByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        List<Post> posts = postRepository.findAllWithMemberByNickname(member.getNickname());
        deleteImagesFromServer(posts);
        memberRepository.delete(member);
    }

    @Transactional
    public MemberPrivateDto updateMember(String username, MemberUpdateRequest request) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        memberUpdate(request, member);
        return MemberPrivateDto.toDto(member);
    }

    @Transactional
    public MemberPrivateDto oauthMemberUpdate(String username, OAuthMemberUpdateRequest request) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        oAuthMemberUpdate(request, member);
        return MemberPrivateDto.toDto(member);
    }

    private void memberUpdate(MemberUpdateRequest request, Member member) {
        validateDuplicateNickname(request.getNickname());
        member.updateMember(request);
        String profileImage = member.updateMemberProfileImage(request);
        profileImageServerUpdate(request.getImage(), member, profileImage);
    }

    private void oAuthMemberUpdate(OAuthMemberUpdateRequest request, Member member) {
        validateDuplicateNickname(request.getNickname());
        String profileImage = member.updateOAuthMember(request);
        profileImageServerUpdate(request.getImage(), member, profileImage);
    }

    private void profileImageServerUpdate(MultipartFile file, Member member, String profileImage) {
        if (file != null) {
            fileService.upload(file, member.getProfileImage().getUniqueName(), FOLDER_NAME_MEMBER_PROFILE);
        } else {
            fileService.delete(profileImage, FOLDER_NAME_MEMBER_PROFILE);
        }
    }

    public void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }
    }

    public void deleteImagesFromServer(List<Post> posts) {
        posts.stream().forEach(p -> {
            List<PostImage> postImages = p.getPostImages();
            postImages.stream().forEach(i -> fileService.delete(i.getUniqueName(), FOLDER_NAME_POST));
        });
    }
}
