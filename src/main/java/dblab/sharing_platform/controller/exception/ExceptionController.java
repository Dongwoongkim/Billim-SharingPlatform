package dblab.sharing_platform.controller.exception;

import dblab.sharing_platform.exception.auth.AccessDeniedException;
import dblab.sharing_platform.exception.auth.AuthenticationEntryPointException;
import dblab.sharing_platform.exception.auth.ValidateTokenException;
import dblab.sharing_platform.exception.guard.GuardException;
import dblab.sharing_platform.exception.token.TokenNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @GetMapping("/access-denied")
    public void accessDeniedException() {
        throw new AccessDeniedException();
    }

    @GetMapping("/entry-point")
    public void authenticateException() {
        throw new AuthenticationEntryPointException();
    }

    @GetMapping("/invalid-token")
    public void validateTokenException() {
        throw new ValidateTokenException();
    }

    @GetMapping("/guard")
    public void guardException() {
        throw new GuardException();
    }

    @GetMapping("/no-token")
    public void tokenNotFoundException() {
        throw new TokenNotFoundException();
    }
}
