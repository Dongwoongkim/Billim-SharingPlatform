package dblab.sharing_platform.service.post;

import dblab.sharing_platform.domain.category.Category;
import dblab.sharing_platform.domain.likepost.LikePost;
import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.dto.post.PostDto;
import dblab.sharing_platform.dto.post.PostCreateRequest;
import dblab.sharing_platform.dto.post.PostCreateResponse;
import dblab.sharing_platform.dto.post.PostPagingCondition;
import dblab.sharing_platform.dto.post.PagedPostListDto;
import dblab.sharing_platform.dto.post.PostReadResponse;
import dblab.sharing_platform.dto.post.PostUpdateRequest;
import dblab.sharing_platform.dto.post.PostUpdateResponse;
import dblab.sharing_platform.exception.post.PostNotFoundException;
import dblab.sharing_platform.helper.NotificationHelper;
import dblab.sharing_platform.repository.category.CategoryRepository;
import dblab.sharing_platform.repository.likepost.LikePostRepository;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dblab.sharing_platform.factory.category.CategoryFactory.createCategoryWithName;
import static dblab.sharing_platform.factory.item.ItemFactory.*;
import static dblab.sharing_platform.factory.member.MemberFactory.*;
import static dblab.sharing_platform.factory.post.PostFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @PersistenceContext
    EntityManager em;

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LikePostRepository likePostRepository;

    @Mock
    private NotificationHelper helper;


    Post post;
    Member member;
    Category category;

    @BeforeEach
    public void beforeEach(){
        post = creatPostWithMemberRole();
        member = post.getMember();
        category = post.getCategory();
    }

    @Test
    @DisplayName("글 생성 테스트")
    public void createPostTest(){
        // Given
        PostCreateRequest postCreateRequest = new PostCreateRequest("테스트 타이틀", "테스트 내용",  category.getName(), List.of(), null);

        given(categoryRepository.findByName(postCreateRequest.getCategoryName())).willReturn(Optional.of(category));
        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));

        // when
        PostCreateResponse result = postService.createPost(postCreateRequest, member.getUsername());

        assertThat(result.getId()).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("글 수정 테스트")
    public void updatePostTest(){
        // Given
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("업데이트 타이틀", "업데이트 내용", null, null, null);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        // When
        PostUpdateResponse result = postService.updatePost(post.getId(), postUpdateRequest);

        // Then
        assertThat(result.getTitle()).isEqualTo("업데이트 타이틀");
    }

    @Test
    @DisplayName("글 삭제 테스트")
    public void deletePostTest(){
        // Given
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        // When
        postService.deletePostByPostId(post.getId());

        // Then
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("아이디로 글 조회 테스트")
    public void readPostTest(){
        // Given
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        // When
        PostReadResponse result = postService.readSinglePostByPostId(post.getId());

        // Then
        assertThat(result.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("전체 글 조회")
    public void readAllPostTest(){
        // Given
        List<Post> posts = new ArrayList<>();
        List<PostDto> postDtoList = new ArrayList<>();

        posts.add(new Post("title 1", "post 1", category, createItem(), List.of(), member));
        posts.add(new Post("title 2", "post 2", category, createItem(), List.of(), member));

        postDtoList.add(PostDto.toDto(posts.get(0)));
        postDtoList.add(PostDto.toDto(posts.get(1)));

        PostPagingCondition cond = new PostPagingCondition(0,10, null, null, null);

        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize(), Sort.by("post_id").ascending());
        Page<PostDto> resultPage = new PageImpl<>(postDtoList, pageable, posts.size());

        given(postRepository.findAllByCategoryAndTitle(cond)).willReturn(resultPage);

        // When
        PagedPostListDto result = postService.readAllPostByCond(cond);

        // Then
        assertThat(result.getPostList()).hasSize(2);

        PostDto postDto1 = result.getPostList().get(0);
        assertThat(postDto1.getTitle()).isEqualTo("title 1");
        assertThat(postDto1.getNickname()).isEqualTo(member.getNickname());

        PostDto postDto2 = result.getPostList().get(1);
        assertThat(postDto2.getTitle()).isEqualTo("title 2");
        assertThat(postDto2.getNickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("제목 조건에 따른 글 조회 테스트")
    public void readAllPostByTitleTest() {
        // Given
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("title 1", "post 1", category, createItem(), List.of(), member));
        posts.add(new Post("title 2", "post 2", category, createItem(), List.of(), member));

        PostPagingCondition cond = new PostPagingCondition(0, 10, null, "title 1", null);

        given(postRepository.findAllByCategoryAndTitle(cond)).willReturn(new PageImpl<>(List.of(PostDto.toDto(posts.get(0))), PageRequest.of(cond.getPage(), cond.getSize()), 1));

        // When
        PagedPostListDto result = postService.readAllPostByCond(cond);

        // Then
        assertThat(result.getPostList()).hasSize(1);

        PostDto postDto1 = result.getPostList().get(0);
        assertThat(postDto1.getTitle()).isEqualTo("title 1");
        assertThat(postDto1.getNickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("카테고리 이름에 따른 글 조회 테스트")
    public void readAllPostByCategoryTest(){
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("title 1", "post 1", createCategoryWithName("category 1"), createItem(), List.of(), member));
        posts.add(new Post("title 2", "post 2", createCategoryWithName("category 2"), createItem(), List.of(), member));

        PostPagingCondition cond = new PostPagingCondition(0, 10, "category 1", null, null);

        given(postRepository.findAllByCategoryAndTitle(cond)).willReturn(new PageImpl<>(List.of(PostDto.toDto(posts.get(0))), PageRequest.of(cond.getPage(), cond.getSize()), 1));

        // When
        PagedPostListDto result = postService.readAllPostByCond(cond);

        // Then
        assertThat(result.getPostList()).hasSize(1);

        PostDto postDto1 = result.getPostList().get(0);
        assertThat(postDto1.getTitle()).isEqualTo("title 1");
        assertThat(postDto1.getNickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("현재 로그인 한 유저가 작성한 글 조회 테스트")
    public void readAllPostByCurrentUser(){
        // Given
        List<Post> posts = new ArrayList<>();
        List<PostDto> postDtoList = new ArrayList<>();
        posts.add(new Post("title 1", "post 1", createCategoryWithName("category 1"), createItem(), List.of(), member));
        posts.add(new Post("title 2", "post 2", createCategoryWithName("category 2"), createItem(), List.of(), member));

        postDtoList.add(PostDto.toDto(posts.get(0)));
        postDtoList.add(PostDto.toDto(posts.get(1)));

        PostPagingCondition cond = new PostPagingCondition(0, 10, null, null, member.getUsername());
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize(), Sort.by("post_id").ascending());

        Page<PostDto> resultPage = new PageImpl<>(postDtoList, pageable, posts.size());

        given(postRepository.findAllWithMemberByCurrentUsername(cond)).willReturn(resultPage);

        // When
        PagedPostListDto result = postService.readAllWriteByCurrentUser(cond);

        // Then
        assertThat(result.getPostList()).hasSize(2);
        PostDto postDto = result.getPostList().get(0);
        assertThat(postDto.getTitle()).isEqualTo("title 1");

        PostDto postDto2 = result.getPostList().get(1);
        assertThat(postDto2.getTitle()).isEqualTo("title 2");
    }

    @Test
    @DisplayName("현재 로그인 한 유저가 좋아요 누른 글 조회하기")
    public void readAllLikesPostByCurrentUser(){
        // Given
        List<LikePost> likePosts = new ArrayList<>();
        likePosts.add(new LikePost(member, new Post("title 1", "post 1", category, createItem(), List.of(), member)));
        likePosts.add(new LikePost(member, new Post("title 2", "post 2", category, createItem(), List.of(), member)));

        List<PostDto> likePostDtoList = new ArrayList<>();
        likePostDtoList.add(PostDto.toDto(likePosts.get(0).getPost()));
        likePostDtoList.add(PostDto.toDto(likePosts.get(1).getPost()));

        PostPagingCondition cond = new PostPagingCondition(0, 10, null, null, member.getUsername());
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize(), Sort.by("post_id").ascending());

        Page<PostDto> resultPage = new PageImpl<>(likePostDtoList, pageable, likePosts.size());

        given(likePostRepository.findAllLikesByCurrentUsername(cond)).willReturn(resultPage);

        // When
        PagedPostListDto result = postService.readAllLikePostByCurrentUser(cond);

        // Then
        assertThat(result.getPostList()).hasSize(2);
        PostDto postDto = result.getPostList().get(0);
        assertThat(postDto.getTitle()).isEqualTo("title 1");

        PostDto postDto2 = result.getPostList().get(1);
        assertThat(postDto2.getTitle()).isEqualTo("title 2");
    }

    @Test
    @DisplayName("글 좋아요 누르기 테스트")
    public void likeUpPostTest(){
        // Given
        post = new Post("new Post", "새 글입니다.", category, createItem(), List.of(), createMember());
        List<LikePost> likePosts = new ArrayList<>();

        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        given(likePostRepository.findAllByPostId(post.getId())).willReturn(likePosts);

        // When
        postService.like(post.getId(), member.getUsername());

        // Then
        assertThat(likePosts.get(0).getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("글 좋아요 취소 테스트")
    public void likeDownPostTest(){
        // Given
        List<LikePost> likePosts = new ArrayList<>();
        LikePost likePost = new LikePost(member, post);
        likePosts.add(likePost);

        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        given(likePostRepository.findAllByPostId(post.getId())).willReturn(likePosts);

        // When
        postService.like(post.getId(), member.getUsername());

        // Then
        verify(likePostRepository).deleteByMemberIdAndPostId(member.getId(), post.getId());
    }
    @Test
    @DisplayName("글 존재 X, 예외 테스트")
    public void postNotFoundExceptionTest(){
        // Given
        Long postId = 100L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.readSinglePostByPostId(postId)).isInstanceOf(PostNotFoundException.class);
    }
}
