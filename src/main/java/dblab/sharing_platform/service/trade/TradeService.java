package dblab.sharing_platform.service.trade;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.domain.trade.Trade;
import dblab.sharing_platform.dto.trade.PagedTradeListDto;
import dblab.sharing_platform.dto.trade.TradePagingCondition;
import dblab.sharing_platform.dto.trade.TradeRequest;
import dblab.sharing_platform.dto.trade.TradeResponse;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.exception.post.PostNotFoundException;
import dblab.sharing_platform.exception.trade.ExistTradeException;
import dblab.sharing_platform.exception.trade.ImpossibleCreateTradeException;
import dblab.sharing_platform.exception.trade.TradeNotFoundException;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import dblab.sharing_platform.repository.trade.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public TradeResponse createTradeByPostId(TradeRequest tradeRequest, Long id, String username) {
        Member render = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        Member borrower = memberRepository.findByNickname(tradeRequest.getBorrowerName())
                .orElseThrow(MemberNotFoundException::new);
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        validateCreateTrade(id, borrower, render);

        Trade trade = new Trade(render, borrower,
                tradeRequest.getStartDate(),
                tradeRequest.getEndDate(),
                post);

        tradeRepository.save(trade);
        return TradeResponse.toDto(trade);
    }

    @Transactional
    public TradeResponse completeTradeByTradeId(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(TradeNotFoundException::new);
        trade.isTradeComplete(true);
        return TradeResponse.toDto(trade);
    }

    public TradeResponse findSingleTradeById(Long id) {
        return TradeResponse.toDto(tradeRepository.findById(id)
                .orElseThrow(TradeNotFoundException::new));
    }

    public PagedTradeListDto findAllTradeByAdmin(TradePagingCondition cond) {
        return PagedTradeListDto.toDto(tradeRepository.findAllByCond(cond));
    }

    public PagedTradeListDto findAllRendTradeByCurrentUser(TradePagingCondition cond) {
        return PagedTradeListDto.toDto((tradeRepository.findAllByMyRend(cond)));
    }

    public PagedTradeListDto findAllBorrowTradeByCurrentUser(TradePagingCondition cond) {
        return PagedTradeListDto.toDto((tradeRepository.findAllByMyBorrow(cond)));
    }

    @Transactional
    public void deleteTradeByRenderWithTradeId(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(TradeNotFoundException::new);
        tradeRepository.delete(trade);
    }

    private void validateCreateTrade(Long id, Member borrower, Member render) {
        if (tradeRepository.findByPostId(id).isPresent()) {
            throw new ExistTradeException();
        }
        
        if (render.getNickname().equals(borrower.getNickname())) {
            throw new ImpossibleCreateTradeException();
        }
    }
}
