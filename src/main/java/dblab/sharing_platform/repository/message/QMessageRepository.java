package dblab.sharing_platform.repository.message;

import dblab.sharing_platform.dto.message.MessageDto;
import dblab.sharing_platform.dto.message.MessagePagingCondition;
import org.springframework.data.domain.Page;

public interface QMessageRepository {

    Page<MessageDto> findAllBySendMember(MessagePagingCondition cond);

    Page<MessageDto> findAllByReceiverMember(MessagePagingCondition cond);
}
