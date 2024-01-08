package dblab.sharing_platform.dto.trade;


import com.fasterxml.jackson.annotation.JsonInclude;
import dblab.sharing_platform.domain.trade.Trade;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeResponse {

    private Long tradeId;
    private Long postId;
    private String renderMember;
    private String borrowerMember;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean tradeComplete;
    private boolean writtenReview;

    public static TradeResponse toDto(Trade trade) {
        if (trade != null) {
            return new TradeResponse(
                    trade.getId(),
                    trade.getPost().getId(),
                    trade.getRenderMember().getNickname(),
                    trade.getBorrowerMember().getNickname(),
                    trade.getStartDate(),
                    trade.getEndDate(),
                    trade.isTradeComplete(),
                    trade.isWrittenReview()
            );
        }

        return null;
    }
}
