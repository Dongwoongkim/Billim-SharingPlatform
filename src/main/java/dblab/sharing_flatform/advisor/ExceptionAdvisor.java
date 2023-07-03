package dblab.sharing_flatform.advisor;

import dblab.sharing_flatform.dto.response.Response;
import dblab.sharing_flatform.exception.ValidateTokenException;
import dblab.sharing_flatform.exception.auth.AccessDeniedException;
import dblab.sharing_flatform.exception.auth.AuthenticationEntryPointException;
import dblab.sharing_flatform.exception.auth.IllegalAuthenticationException;
import dblab.sharing_flatform.exception.auth.LoginFailureException;
import dblab.sharing_flatform.exception.image.NoExtException;
import dblab.sharing_flatform.exception.image.UnSupportExtException;
import dblab.sharing_flatform.exception.member.DuplicateUsernameException;
import dblab.sharing_flatform.exception.member.MemberNotFoundException;
import dblab.sharing_flatform.exception.role.RoleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvisor {

    // uncaught Exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e) {
        e.printStackTrace();
        return Response.failure(500, e.getMessage());
    }

    // Field Error
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response bindException(BindException e) {
        log.info("message = {}", e.getMessage());
        return Response.failure(400, "양식에 맞게 입력해주세요.");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Response.failure(400, "양식에 맞게 입력해주세요.");
    }

    // auth
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response accessDeniedException(AccessDeniedException e) {
        return Response.failure(400, "해당 권한으로 수행할 수 없는 작업입니다.");
    }


    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response authenticationEntryPointException(AuthenticationEntryPointException e) {
        return Response.failure(400, "로그인이 필요한 요청입니다.");
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response loginFailureException(LoginFailureException e) {
        return Response.failure(400, "로그인에 실패하였습니다. 아이디나 비밀번호를 확인해주세요.");
    }

    @ExceptionHandler(IllegalAuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response illegalAuthenticationException(IllegalAuthenticationException e) {
        return Response.failure(400, "올바르지 않은 인증정보입니다.");
    }

    @ExceptionHandler(ValidateTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response validateTokenException(ValidateTokenException e) {
        return Response.failure(400, "검증되지 않은 토큰 정보입니다.");
    }

    // image
    @ExceptionHandler(NoExtException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response noExtException(NoExtException e) {
        return Response.failure(404, "사진의 확장자를 찾을 수 없습니다.");
    }

    @ExceptionHandler(UnSupportExtException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Response unSupportExtException(UnSupportExtException e) {
        return Response.failure(415, "지원하지 않는 미디어 타입입니다.");
    }






    // member
    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Response duplicateUsernameException(DuplicateUsernameException e) {
        return Response.failure(406, "이미 사용중인 Username입니다.");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundException(MemberNotFoundException e) {
        return Response.failure(404, "회원을 찾을 수 없습니다.");
    }



    // role
    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response roleNotFoundException(RoleNotFoundException e) {
        return Response.failure(404, "권한을 찾을 수 없습니다.");
    }



}
