package dblab.sharing_flatform.domain.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dblab.sharing_flatform.factory.message.MessageFactory.createMessage;
import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    Message message;

    @BeforeEach
    void beforeEach() {
        Message message = createMessage();
    }

    @Test
    void deleteBySenderTest() {
        // given
        Message message = createMessage();
        // when
        message.deleteBySender();

        // then
        assertThat(message.isDeleteBySender()).isTrue();
    }

    @Test
    void deleteByReceiverTest() {
        // given
        Message message = createMessage();
        // when
        message.deleteByReceiver();

        // then
        assertThat(message.isDeleteByReceiver()).isTrue();
    }

    @Test
    public void isNotDeletableTest() throws Exception {
        //given
        Message message = createMessage();
        // then
        assertThat(message.isDeletable()).isFalse();
    }

    @Test
    void isDeletableTest() {
        // given
        Message message = createMessage();
        message.deleteBySender();
        message.deleteByReceiver();

        // when
        boolean deletable = message.isDeletable();

        // then
        assertThat(deletable).isTrue();
    }
}