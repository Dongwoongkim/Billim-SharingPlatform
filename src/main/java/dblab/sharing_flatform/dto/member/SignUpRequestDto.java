package dblab.sharing_flatform.dto.member;

import dblab.sharing_flatform.domain.address.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Embedded;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(value = "회원가입 요청")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequestDto {

    @ApiModelProperty(value = "username", notes = "사용자 이름은 한글 또는 알파벳으로 입력해주세요.", required = true, example = "kimdongwoong")
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min=2, message = "사용자 이름이 너무 짧습니다.")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "사용자 이름은 한글 또는 알파벳만 입력해주세요.")
    private String username;


    @ApiModelProperty(value = "password", notes = "비밀번호는 최소 8자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.", required = true, example = "123456a!")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;


    @ApiModelProperty(value = "phoneNumber", notes = "전화번호를 입력해주세요.", required = true, example = "01012345678")
    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phoneNumber;

    @ApiModelProperty(value = "address", notes = "", required = true)
    @NotNull(message = "주소는 반드시 입력해야 합니다.")
    @Embedded
    private Address address;

}
