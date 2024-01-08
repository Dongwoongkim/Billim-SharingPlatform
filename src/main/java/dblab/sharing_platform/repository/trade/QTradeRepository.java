package dblab.sharing_platform.repository.trade;

import dblab.sharing_platform.dto.trade.TradeDto;
import dblab.sharing_platform.dto.trade.TradePagingCondition;
import org.springframework.data.domain.Page;

public interface QTradeRepository {

    Page<TradeDto> findAllByCond(TradePagingCondition cond);

    Page<TradeDto> findAllByMyRend(TradePagingCondition cond);

    Page<TradeDto> findAllByMyBorrow(TradePagingCondition cond);
}
