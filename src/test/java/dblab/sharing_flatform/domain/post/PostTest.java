package dblab.sharing_flatform.domain.post;

import dblab.sharing_flatform.domain.image.Image;
import org.junit.jupiter.api.Test;

import java.util.List;
import static dblab.sharing_flatform.factory.post.PostFactory.createPost;
import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    public void createPostTest() throws Exception {
        //given
        Post post = createPost();

        //when
        Integer likes = post.getLikes();

        //then
        assertThat(likes).isEqualTo(0);
    }

    @Test
    public void addImagesTest() throws Exception {
        //given
        Post post = createPost();

        //when
        List<Image> images = post.getImages();

        //then
        images.stream().forEach( i -> assertThat(i.getPost()).isEqualTo(post));
    }

}
