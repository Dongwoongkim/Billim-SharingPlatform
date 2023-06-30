package dblab.sharing_flatform.controller;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// 시큐리티 적용 테스트 컨트롤러
@RestController
public class AuthController {

    @GetMapping("/adminPage")
    public String adminPage() {
        return "adminPage";
    }

    @GetMapping("/managerPage")
    public String managerPage() {
        return "managerPage";
    }

    @GetMapping("/userPage")
    public String userPage() {
        return "userPage";
    }

    @GetMapping("/authenticate")
    public String authenticatePage() {
        return "authenticatePage";
    }
}
