package dblab.sharing_platform.dto.response_no_usage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Response {

    private boolean success;
    private int code;
    private Result result;

    public static Response success(int code) {
        return new Response(true, code, null);
    }

    public static <T> Response success(int code, T data) {
        return new Response(true, code, new Success<>(data));
    }

    public static Response failure(int code, String msg) {
        return new Response(false, code, new Failure(msg));
    }
}
