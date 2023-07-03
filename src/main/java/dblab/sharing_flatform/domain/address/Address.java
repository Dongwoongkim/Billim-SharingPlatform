package dblab.sharing_flatform.domain.address;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @ApiModelProperty(value = "city", required = true, example = "전주시")
    @NotBlank(message = "필수 입력 값입니다.")
    private String city; // 도시

    @ApiModelProperty(value = "disrict", required = true, example = "덕진구")
    @NotBlank(message = "필수 입력 값입니다.")
    private String district; // 구

    @ApiModelProperty(value = "city", required = true, example = "금암동 1길")
    @NotBlank(message = "필수 입력 값입니다.")
    private String street; // 상세 주소

    @ApiModelProperty(value = "zipCode", required = true, example = "54475")
    @NotBlank(message = "필수 입력 값입니다.")
    private String zipCode; // 우편번호

}
