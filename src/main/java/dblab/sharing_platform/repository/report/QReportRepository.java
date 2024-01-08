package dblab.sharing_platform.repository.report;

import dblab.sharing_platform.dto.report.ReportDto;
import dblab.sharing_platform.dto.report.ReportPagingCondition;
import org.springframework.data.domain.Page;

public interface QReportRepository {
    
    Page<ReportDto> findAllByCond(ReportPagingCondition cond);

    Page<ReportDto> findAllMyReportByCond(ReportPagingCondition cond);
}
