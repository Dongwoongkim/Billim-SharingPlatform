package dblab.sharing_platform.advisor;

import dblab.sharing_platform.dto.response_no_usage.Response;
import dblab.sharing_platform.exception.auth.AccessDeniedException;
import dblab.sharing_platform.exception.auth.AlreadyExistsMemberException;
import dblab.sharing_platform.exception.auth.AlreadySendAuthKeyException;
import dblab.sharing_platform.exception.auth.AuthenticationEntryPointException;
import dblab.sharing_platform.exception.auth.DuplicateNicknameException;
import dblab.sharing_platform.exception.auth.DuplicateUsernameException;
import dblab.sharing_platform.exception.auth.EmailAuthNotEqualsException;
import dblab.sharing_platform.exception.auth.EmailAuthNotFoundException;
import dblab.sharing_platform.exception.auth.IllegalAuthenticationException;
import dblab.sharing_platform.exception.auth.LoginFailureException;
import dblab.sharing_platform.exception.auth.NotEqualsPasswordToVerifiedException;
import dblab.sharing_platform.exception.auth.ValidateTokenException;
import dblab.sharing_platform.exception.category.CategoryNotFoundException;
import dblab.sharing_platform.exception.comment.CommentNotFoundException;
import dblab.sharing_platform.exception.comment.RootCommentNotFoundException;
import dblab.sharing_platform.exception.file.FileUploadFailureException;
import dblab.sharing_platform.exception.guard.GuardException;
import dblab.sharing_platform.exception.helper.ConvertException;
import dblab.sharing_platform.exception.image.NoExtException;
import dblab.sharing_platform.exception.image.UnSupportExtException;
import dblab.sharing_platform.exception.member.MemberNotFoundException;
import dblab.sharing_platform.exception.message.MessageNotFoundException;
import dblab.sharing_platform.exception.message.SendMessageException;
import dblab.sharing_platform.exception.oauth.OAuthCommunicationException;
import dblab.sharing_platform.exception.oauth.OAuthUserNotFoundException;
import dblab.sharing_platform.exception.oauth.SocialAgreementException;
import dblab.sharing_platform.exception.post.PostNotFoundException;
import dblab.sharing_platform.exception.report.ReportNotFoundException;
import dblab.sharing_platform.exception.review.ExistReviewException;
import dblab.sharing_platform.exception.review.ImpossibleWriteReviewException;
import dblab.sharing_platform.exception.review.ReviewNotFoundException;
import dblab.sharing_platform.exception.role.RoleNotFoundException;
import dblab.sharing_platform.exception.token.TokenNotFoundException;
import dblab.sharing_platform.exception.trade.ExistTradeException;
import dblab.sharing_platform.exception.trade.ImpossibleCreateTradeException;
import dblab.sharing_platform.exception.trade.TradeNotCompleteException;
import dblab.sharing_platform.exception.trade.TradeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvisor {

    private final MessageSource ms;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e) {
        e.printStackTrace();
        return getFailureResponse("INTERNAL_SERVER_ERROR.CODE", "EXCEPTION.MSG");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response bindException(BindException e) {
        e.printStackTrace();
        return getFailureResponse("BAD_REQUEST.CODE", "FIELD_ERROR.MSG");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response methodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        return getFailureResponse("BAD_REQUEST.CODE", "FIELD_ERROR.MSG");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response accessDeniedException(AccessDeniedException e) {
        return getFailureResponse("UNAUTHORIZED.CODE", "ACCESS_DENIED.MSG");
    }

    @ExceptionHandler(AlreadyExistsMemberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response alreadyExistsMemberException(AlreadyExistsMemberException e) {
        return getFailureResponse("CONFLICT.CODE", "OAUTH2_ALREADY_SIGNUP.MSG");
    }

    @ExceptionHandler(AlreadySendAuthKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response alreadySendAuthKeyException(AlreadySendAuthKeyException e) {
        return getFailureResponse("CONFLICT.CODE", "OAUTH2_ALREADY_SEND_KEY.MSG");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)
    public Response authenticationEntryPointException(AuthenticationEntryPointException e) {
        e.printStackTrace();
        return getFailureResponse("NETWORK_AUTHENTICATION_REQUIRED.CODE", "REQUIRED_AUTHENTICATE.MSG");
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response duplicateNicknameException(DuplicateNicknameException e) {
        return getFailureResponse("CONFLICT.CODE", "DUPLICATE_NICKNAME.MSG");
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response duplicateUsernameException(DuplicateUsernameException e) {
        return getFailureResponse("CONFLICT.CODE", "COMMON_ALREADY_SIGNUP.MSG");
    }

    @ExceptionHandler(EmailAuthNotEqualsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response emailAuthNotEqualsException(EmailAuthNotEqualsException e) {
        return getFailureResponse("CONFLICT.CODE", "EMAIL_AUTH_NOT_EQUAL.MSG");
    }

    @ExceptionHandler(EmailAuthNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response emailAuthNotFoundException(EmailAuthNotFoundException e) {
        return getFailureResponse("INTERNAL_SERVER_ERROR.CODE", "EMAIL_AUTH_FAIL.MSG");
    }

    @ExceptionHandler(IllegalAuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response illegalAuthenticationException(IllegalAuthenticationException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "ILLEGAL_AUTHENTICATION.MSG");
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response loginFailureException(LoginFailureException e) {
        return getFailureResponse("CONFLICT.CODE", "LOGIN_FAILURE.MSG");
    }

    @ExceptionHandler(NotEqualsPasswordToVerifiedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response notEqualsPasswordToVerifiedException(NotEqualsPasswordToVerifiedException e) {
        return getFailureResponse("CONFLICT.CODE", "NOT_EQUAL_PASSWORD_VERIFIED.MSG");
    }

    @ExceptionHandler(ValidateTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response validateTokenException(ValidateTokenException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "INVALID_TOKEN.MSG");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException(CategoryNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "CATEGORY_NOT_FOUND.MSG");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response commentNotFoundException(CommentNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "COMMENT_NOT_FOUND.MSG");
    }

    @ExceptionHandler(RootCommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response rootCommentNotFoundException(RootCommentNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "ROOT_COMMENT_NOT_FOUND.MSG");
    }

    @ExceptionHandler(FileUploadFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response fileUploadFailureException(FileUploadFailureException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "FILE_UPLOAD.MSG");
    }

    @ExceptionHandler(GuardException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response guardException(GuardException e) {
        return getFailureResponse("UNAUTHORIZED.CODE", "GUARD.MSG");
    }

    @ExceptionHandler(ConvertException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response convertException(ConvertException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "CONVERTER.MSG");
    }

    @ExceptionHandler(NoExtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response noExtException(NoExtException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "NOT_EXIST_EXT.MSG");
    }

    @ExceptionHandler(UnSupportExtException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Response unSupportExtException(UnSupportExtException e) {
        return getFailureResponse("UNSUPPORTED_MEDIA_TYPE.CODE", "UNSUPPORTED_TYPE.MSG");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundException(MemberNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "MEMBER_NOT_FOUND.MSG");
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response messageNotFoundException(MessageNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "MESSAGE_NOT_FOUND.MSG");
    }

    @ExceptionHandler(SendMessageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response sendMessageException(SendMessageException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "SEND_MESSAGE_BAD_REQUEST.MSG");
    }

    @ExceptionHandler(OAuthCommunicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response oAuthCommunicationException(OAuthCommunicationException e) {
        return getFailureResponse("INTERNAL_SERVER_ERROR.CODE", "OAUTH_LOGOUT.MSG");
    }

    @ExceptionHandler(OAuthUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response oAuthUserNotFoundException(OAuthUserNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "OAUTH_MEMBER_NOT_FOUND.MSG");
    }

    @ExceptionHandler(SocialAgreementException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    public Response socialAgreementException(SocialAgreementException e) {
        return getFailureResponse("PRECONDITION_REQUIRED.CODE", "SOCIAL_AGREEMENT.MSG");
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response postNotFoundException(PostNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "POST_NOT_FOUND.MSG");
    }

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response reportNotFoundException(ReportNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "REPORT_NOT_FOUND.MSG");
    }

    @ExceptionHandler(ExistReviewException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response ExistReviewException(ExistReviewException e) {
        return getFailureResponse("CONFLICT.CODE", "EXIST_REVIEW.MSG");
    }

    @ExceptionHandler(ImpossibleWriteReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response impossibleWriteReviewException(ImpossibleWriteReviewException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "WRITE_REVIEW.MSG");
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response reviewNotFoundException(ReviewNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "REVIEW_NOT_FOUND.MSG");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response roleNotFoundException(RoleNotFoundException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "ROLE_NOT_FOUND.MSG");
    }

    @ExceptionHandler(ExistTradeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response ExistTradeException(ExistTradeException e) {
        return getFailureResponse("CONFLICT.CODE", "EXIST_TRADE.MSG");
    }

    @ExceptionHandler(ImpossibleCreateTradeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response impossibleCreateTradeException(ImpossibleCreateTradeException e) {
        return getFailureResponse("BAD_REQUEST.CODE", "IMPOSSIBLE_CREATE_TRADE.MSG");
    }

    @ExceptionHandler(TradeNotCompleteException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    public Response tradeNotCompleteException(TradeNotCompleteException e) {
        return getFailureResponse("PRECONDITION_REQUIRED.CODE", "TRADE_NOT_COMPLETE.MSG");
    }

    @ExceptionHandler(TradeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response tradeNotFoundException(TradeNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "TRADE_NOT_FOUND.MSG");
    }

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response tokenNotFoundException(TokenNotFoundException e) {
        return getFailureResponse("NOT_FOUND.CODE", "TOKEN_NOT_FOUND.MSG");
    }

    private Response getFailureResponse(String code, String msg) {
        return Response.failure(getCode(code), getMessage(msg));
    }

    private Integer getCode(String code) {
        return Integer.valueOf(ms.getMessage(code, null, null));
    }

    private String getMessage(String msg) {
        return ms.getMessage(msg, null, LocaleContextHolder.getLocale());
    }
}
