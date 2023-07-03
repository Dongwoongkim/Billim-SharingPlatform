package dblab.sharing_flatform.dto.member;

import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@ApiModel(value = "로그인 응답")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LogInResponseDto {

    private String token;
    public static LogInResponseDto toDto(String token) {
        return new LogInResponseDto(token);
    }
}
