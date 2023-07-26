package dblab.sharing_flatform.service.trade;

import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.trade.Trade;
import dblab.sharing_flatform.dto.trade.crud.create.TradeRequestDto;
import dblab.sharing_flatform.dto.trade.crud.create.TradeResponseDto;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.exception.post.PostNotFoundException;
import dblab.sharing_flatform.exception.trade.ExistTradeException;
import dblab.sharing_flatform.exception.trade.ImpossibleCreateTradeException;
import dblab.sharing_flatform.exception.trade.TradeNotFoundException;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.post.PostRepository;
import dblab.sharing_flatform.repository.trade.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public TradeResponseDto createTrade(TradeRequestDto tradeRequestDto, Long id){
        Member render = memberRepository.findByUsername(tradeRequestDto.getRenderName()).orElseThrow(MemberNotFoundException::new);
        Member borrower = memberRepository.findByNickname(tradeRequestDto.getBorrowerName()).orElseThrow(MemberNotFoundException::new);

        validateCreateTrade(id, borrower, render);

        Trade trade = new Trade(render, borrower,
                        tradeRequestDto.getStartDate(),
                        tradeRequestDto.getEndDate(),
                        postRepository.findById(id).orElseThrow(PostNotFoundException::new));

        tradeRepository.save(trade);
        return TradeResponseDto.toDto(trade);

    }

    @Transactional
    public TradeResponseDto completeTrade(Long id) {
        Trade trade = tradeRepository.findById(id).orElseThrow(TradeNotFoundException::new);
        trade.isTradeComplete(true);
        return TradeResponseDto.toDto(trade);
    }

    public TradeResponseDto findTradeById(Long id) {
        return TradeResponseDto.toDto(tradeRepository.findById(id).orElseThrow(TradeNotFoundException::new));
    }

    public List<TradeResponseDto> findAllTrade() {
        return tradeRepository.findAll().stream().map(trade -> TradeResponseDto.toDto(trade)).collect(Collectors.toList());
    }

    @Transactional
    public void deleteTrade(Long id) {
        Trade trade = tradeRepository.findById(id).orElseThrow(TradeNotFoundException::new);
        tradeRepository.delete(trade);
    }

    private void validateCreateTrade(Long id, Member borrower, Member render) {
        tradeRepository.findByPostId(id).ifPresent(e -> {
            throw new ExistTradeException();
        });
        if (render.getNickname().equals(borrower.getNickname())) {
            throw new ImpossibleCreateTradeException();
        }
    }
}
