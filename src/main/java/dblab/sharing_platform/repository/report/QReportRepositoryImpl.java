package dblab.sharing_platform.repository.report;

import static com.querydsl.core.types.Projections.constructor;
import static dblab.sharing_platform.domain.report.QReport.report;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dblab.sharing_platform.domain.report.Report;
import dblab.sharing_platform.dto.report.ReportDto;
import dblab.sharing_platform.dto.report.ReportPagingCondition;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

public class QReportRepositoryImpl extends QuerydslRepositorySupport implements QReportRepository {

    private final JPAQueryFactory query;

    public QReportRepositoryImpl(JPAQueryFactory query) {
        super(Report.class);
        this.query = query;
    }

    @Override
    public Page<ReportDto> findAllByCond(ReportPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateForAdmin(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    @Override
    public Page<ReportDto> findAllMyReportByCond(ReportPagingCondition cond) {
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        Predicate predicate = createPredicateForMyReport(cond);
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    private Predicate createPredicateForAdmin(ReportPagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(cond.getWriterNickname())) {
            builder.and(report.reporter.nickname.eq(cond.getWriterNickname()));
        }
        if (StringUtils.hasText(cond.getReportedNickname())) {
            builder.and(report.reported.nickname.eq(cond.getReportedNickname()));
        }
        if (cond.getReportType() != null) {
            builder.and(report.reportType.eq(cond.getReportType()));
        }
        return builder;
    }

    private Predicate createPredicateForMyReport(ReportPagingCondition cond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(cond.getWriterUsername())) {
            builder.and(report.reporter.username.eq(cond.getWriterUsername()));
        }
        if (StringUtils.hasText(cond.getReportedNickname())) {
            builder.and(report.reported.nickname.eq(cond.getReportedNickname()));
        }
        if (cond.getReportType() != null) {
            builder.and(report.reportType.eq(cond.getReportType()));
        }
        return builder;
    }

    private List<ReportDto> fetchAll(Predicate predicate, Pageable pageable) {
        return getQuerydsl().applyPagination(
                pageable,
                query.select(constructor(ReportDto.class,
                                report.id,
                                report.reporter.nickname,
                                report.reportType,
                                report.content,
                                report.post.id,
                                report
                        ))
                        .from(report)
                        .where(predicate)
                        .orderBy(report.id.asc())
        ).fetch();
    }

    private Long fetchCount(Predicate predicate) {
        return query.select(report.count())
                .from(report)
                .where(predicate)
                .fetchOne();
    }
}
