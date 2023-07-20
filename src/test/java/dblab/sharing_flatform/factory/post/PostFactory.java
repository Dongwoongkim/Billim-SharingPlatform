package dblab.sharing_flatform.factory.post;

import dblab.sharing_flatform.domain.category.Category;
import dblab.sharing_flatform.domain.image.PostImage;
import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.post.Post;

import java.util.List;

import static dblab.sharing_flatform.factory.category.CategoryFactory.createCategory;
import static dblab.sharing_flatform.factory.item.ItemFactory.createItem;
import static dblab.sharing_flatform.factory.member.MemberFactory.createMember;

public class PostFactory {
    public static Post createPost() {
        return new Post("title", "content", createCategory(), createItem(), List.of(), createMember());
    }

    public static Post createPost(List<PostImage> postImages) {
        return new Post("title", "content", createCategory(), createItem(), postImages, createMember());
    }

    public static Post createPost(Category category, Member member) {
        return new Post("title", "content", category, createItem(), List.of(), member);
    }


}
