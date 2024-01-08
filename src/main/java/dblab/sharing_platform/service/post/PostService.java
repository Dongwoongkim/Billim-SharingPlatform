package dblab.sharing_platform.service.post;

import static dblab.sharing_platform.config.file.FileInfo.FOLDER_NAME_POST;

import dblab.sharing_platform.domain.category.Category;
import dblab.sharing_platform.domain.image.PostImage;
import dblab.sharing_platform.domain.likepost.LikePost;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.notification.NotificationType;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.dto.image.PostImageDto;
import dblab.sharing_platform.dto.item.ItemCreateRequest;
import dblab.sharing_platform.dto.post.PagedPostListDto;
import dblab.sharing_platform.dto.post.PostCreateRequest;
import dblab.sharing_platform.dto.post.PostCreateResponse;
import dblab.sharing_platform.dto.post.PostDto;
import dblab.sharing_platform.dto.post.PostPagingCondition;
import dblab.sharing_platform.dto.post.PostReadResponse;
import dblab.sharing_platform.dto.post.PostUpdateRequest;
import dblab.sharing_platform.dto.post.PostUpdateResponse;
import dblab.sharing_platform.exception.auth.AuthenticationEntryPointException;
import dblab.sharing_platform.exception.category.CategoryNotFoundException;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.exception.post.PostNotFoundException;
import dblab.sharing_platform.helper.NotificationHelper;
import dblab.sharing_platform.repository.category.CategoryRepository;
import dblab.sharing_platform.repository.likepost.LikePostRepository;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import dblab.sharing_platform.service.file.FileService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final LikePostRepository likePostRepository;
    private final NotificationHelper notificationHelper;
    private static final String LIKE_POST_MESSAGE = "님이 이 글을 좋아합니다.";

    public PagedPostListDto readAllPostByCond(PostPagingCondition cond) {
        return PagedPostListDto.toDto(postRepository.findAllByCategoryAndTitle(cond));
    }

    public PagedPostListDto readAllLikePostByCurrentUser(PostPagingCondition cond) {
        return PagedPostListDto.toDto(likePostRepository.findAllLikesByCurrentUsername(cond));
    }

    public PagedPostListDto readAllWriteByCurrentUser(PostPagingCondition cond) {
        Page<PostDto> page = postRepository.findAllWithMemberByCurrentUsername(cond);
        return PagedPostListDto.toDto(page);
    }

    public PostReadResponse readSinglePostByPostId(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
        return PostReadResponse.toDto(post);
    }

    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request, String username) {
        List<PostImage> postImages = getImages(request);
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(CategoryNotFoundException::new);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(AuthenticationEntryPointException::new);

        Post post = new Post(request.getTitle(),
                request.getContent(),
                category,
                request.getItemCreateRequest() != null ? ItemCreateRequest.toEntity(request.getItemCreateRequest())
                        : null,
                postImages,
                member);

        postRepository.save(post);

        return PostCreateResponse.toDto(post);
    }

    @Transactional
    public void deletePostByPostId(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        postRepository.delete(post);
        deleteImagesFromServer(post);
    }

    @Transactional
    public PostUpdateResponse updatePost(Long id, PostUpdateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        PostUpdateResponse response = post.updatePost(request);
        updateImagesToServer(request, response);

        return response;
    }

    @Transactional
    public void like(Long id, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);

        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        likeUpOrDown(member, post);
    }

    public void uploadImagesToServer(List<PostImage> postImages, List<MultipartFile> fileImages) {
        if (postImages.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < postImages.size(); i++) {
            fileService.upload(fileImages.get(i), postImages.get(i).getUniqueName(), FOLDER_NAME_POST);
        }
    }

    public void deleteImagesFromServer(Post post) {
        List<PostImage> postImages = post.getPostImages();
        postImages.stream().forEach(i -> fileService.delete(i.getUniqueName(), FOLDER_NAME_POST));
    }

    public void updateImagesToServer(PostUpdateRequest request, PostUpdateResponse response) {
        uploadImagesToServer(request, response);
        deleteImagesFromServer(response);
    }

    private void uploadImagesToServer(PostUpdateRequest request, PostUpdateResponse response) {
        List<PostImageDto> addedImages = response.getAddedImages();

        for (int i = 0; i < addedImages.size(); i++) {
            fileService.upload(request.getAddImages().get(i), addedImages.get(i).getUniqueName(), FOLDER_NAME_POST);
        }
    }

    private void deleteImagesFromServer(PostUpdateResponse response) {
        List<PostImageDto> deletePostImageDtoList = response.getDeletedImages();
        deletePostImageDtoList.stream().forEach(i -> fileService.delete(i.getUniqueName(), FOLDER_NAME_POST));
    }

    private List<PostImage> getImages(PostCreateRequest request) {
        List<PostImage> postImages;

        if (request.getMultipartFiles() != null) {
            postImages = MultiPartFileToImage(request.getMultipartFiles());
            uploadImagesToServer(postImages, request.getMultipartFiles());
            return postImages;
        }

        return List.of();
    }

    private List<PostImage> MultiPartFileToImage(List<MultipartFile> multipartFiles) {
        return multipartFiles.stream().map(i -> new PostImage(i.getOriginalFilename())).collect(Collectors.toList());
    }

    private void likeUpOrDown(Member member, Post post) {
        List<LikePost> likePosts = likePostRepository.findAllByPostId(post.getId());
        Optional<LikePost> likePost = validateAlreadyLikeUp(member, likePosts);

        likeDownIfLike(member, post, likePost, likePosts);
        likeUpIfNotLike(member, post, likePost, likePosts);
    }

    private void likeDownIfLike(Member member, Post post, Optional<LikePost> likePost, List<LikePost> likePosts) {
        if (likePost.isPresent()) {
            post.likeDown();
            likePosts.remove(likePost.get());
            likePostRepository.deleteByMemberIdAndPostId(member.getId(), post.getId());
        }
    }

    private void likeUpIfNotLike(Member member, Post post, Optional<LikePost> likePost, List<LikePost> likePosts) {
        if (!likePost.isPresent()) {
            post.likeUp();
            likePosts.add(new LikePost(member, post));
            likePostRepository.save(new LikePost(member, post));
            Member writeMember = memberRepository.findByUsername(post.getMember().getUsername())
                    .orElseThrow(MemberNotFoundException::new);
            notify(member, writeMember);
        }
    }

    private void notify(Member member, Member writeMember) {
        notificationHelper.notificationIfSubscribe(member, writeMember, NotificationType.LIKE, LIKE_POST_MESSAGE);
    }

    private Optional<LikePost> validateAlreadyLikeUp(Member member, List<LikePost> likePosts) {
        return likePosts.stream()
                .filter(lp -> lp.getMember().equals(member))
                .findFirst();
    }
}
