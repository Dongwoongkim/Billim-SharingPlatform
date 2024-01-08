package dblab.sharing_platform.service.report;

import dblab.sharing_platform.domain.member.Member;
import dblab.sharing_platform.domain.post.Post;
import dblab.sharing_platform.domain.report.Report;
import dblab.sharing_platform.dto.report.PagedReportListDto;
import dblab.sharing_platform.dto.report.ReportCreateRequest;
import dblab.sharing_platform.dto.report.ReportPagingCondition;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.exception.post.PostNotFoundException;
import dblab.sharing_platform.exception.report.ReportNotFoundException;
import dblab.sharing_platform.repository.member.MemberRepository;
import dblab.sharing_platform.repository.post.PostRepository;
import dblab.sharing_platform.repository.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createReportToPostOrUser(ReportCreateRequest request, String username) {
        Post post = request.getPostId() == null ? null : postRepository.findById(Long.valueOf(request.getPostId()))
                .orElseThrow(PostNotFoundException::new);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);

        reportRepository.save(new Report(request.getReportType(), request.getContent(), member, post,
                post == null ? null : post.getMember()));
    }

    @Transactional
    public void deleteReportByReportId(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);
        reportRepository.delete(report);
    }

    public PagedReportListDto readAllReportByCond(ReportPagingCondition cond) {
        return PagedReportListDto.toDto(reportRepository.findAllByCond(cond));
    }

    public PagedReportListDto readAllMyReport(ReportPagingCondition cond) {
        return PagedReportListDto.toDto(reportRepository.findAllMyReportByCond(cond));
    }
}
