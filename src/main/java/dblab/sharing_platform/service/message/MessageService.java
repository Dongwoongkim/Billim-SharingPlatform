package dblab.sharing_platform.service.message;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.message.Message;
import dblab.sharing_platform.domain.notification.NotificationType;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.dto.message.MessageCreateRequest;
import dblab.sharing_platform.dto.message.MessageDto;
import dblab.sharing_platform.dto.message.MessagePagingCondition;
import dblab.sharing_platform.dto.message.PagedMessageListDto;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.exception.message.MessageNotFoundException;
import dblab.sharing_platform.exception.message.SendMessageException;
import dblab.sharing_platform.exception.post.PostNotFoundException;
import dblab.sharing_platform.helper.NotificationHelper;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.message.MessageRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private static final String ARRIVE_MESSAGE = "님으로부터 메시지가 도착했습니다.";

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final NotificationHelper notificationHelper;

    @Transactional
    public MessageDto findMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(MessageNotFoundException::new);
        message.readByReceiver();
        return MessageDto.toDto(message);
    }

    @Transactional
    public MessageDto sendMessageToReceiverByCurrentUser(MessageCreateRequest request, String username) {
        Member sender = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        Member receiver = memberRepository.findByNickname(request.getReceiveMember())
                .orElseThrow(MemberNotFoundException::new);

        Post post = postValidation(Long.valueOf(request.getPostId()), receiver, sender);

        Message message = new Message(request.getContent(), receiver, sender, post);

        messageRepository.save(message);
        notificationHelper.notificationIfSubscribe(sender, receiver, NotificationType.MESSAGE, ARRIVE_MESSAGE);

        return MessageDto.toDto(message);
    }

    public PagedMessageListDto findSendMessageByCurrentUser(MessagePagingCondition cond) {
        return PagedMessageListDto.toDto(messageRepository.findAllBySendMember(cond));
    }

    public PagedMessageListDto findReceiveMessageByCurrentUser(MessagePagingCondition cond) {
        return PagedMessageListDto.toDto(messageRepository.findAllByReceiverMember(cond));
    }

    @Transactional
    public void deleteMessageBySender(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(MessageNotFoundException::new);
        message.deleteBySender();

        if (deletedByBothSides(message)) {
            messageRepository.delete(message);
        }
    }

    private boolean deletedByBothSides(Message message) {
        return message.isDeleteBySender() && message.isDeleteByReceiver();
    }

    @Transactional
    public void deleteMessageByReceiver(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(MessageNotFoundException::new);
        message.deleteByReceiver();

        if (deletedByBothSides(message)) {
            messageRepository.delete(message);
        }
    }

    private Post postValidation(Long postId, Member receiver, Member sender) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (isEqualToPostWriter(receiver, post) || // 게시글 작성자와 메시지 수신자가 일치하거나
                (isEqualToPostWriter(sender, post) && existedPreviousMessage(receiver,
                        post))) { // 게시글에 대해 받은 메시지가 있는 경우
            return post;
        } else {
            throw new SendMessageException();
        }
    }

    private boolean existedPreviousMessage(Member receiver, Post post) {
        return messageRepository.findBySenderIdAndPostId(receiver.getId(), post.getId()).isPresent();
    }

    private boolean isEqualToPostWriter(Member receiver, Post post) {
        return post.getMember().getNickname().equals(receiver.getNickname());
    }
}
