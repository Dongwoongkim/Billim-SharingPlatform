package dblab.sharing_platform.dto.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "로그인 요청")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {

    @ApiModelProperty(value = "username", notes = "USERNAME를 입력해주세요", required = true)
    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    @ApiModelProperty(value = "password", notes = "비밀번호를 입력해주세요", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
