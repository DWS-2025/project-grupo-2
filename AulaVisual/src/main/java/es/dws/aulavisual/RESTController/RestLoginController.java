package es.dws.aulavisual.RESTController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.dws.aulavisual.security.jwt.AuthResponse;
import es.dws.aulavisual.security.jwt.AuthResponse.Status;
import es.dws.aulavisual.security.jwt.LoginRequest;
import es.dws.aulavisual.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/auth/")
public class RestLoginController {

    private final UserLoginService userLoginService;

    public RestLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PostMapping("login/")
    public ResponseEntity <AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        return userLoginService.login(response, loginRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

        return userLoginService.refresh(response, refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
        return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userLoginService.logout(response)));
    }
}
