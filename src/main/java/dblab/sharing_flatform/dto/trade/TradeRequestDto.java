package dblab.sharing_flatform.dto.trade;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRequestDto {

    private String borrowerName;

    @JsonFormat(pattern = "yyyy.MM.dd") //데이터 포맷 변환
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy.MM.dd") //데이터 포맷 변환
    private LocalDate endDate;
}