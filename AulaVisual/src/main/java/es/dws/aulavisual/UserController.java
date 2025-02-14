package es.dws.aulavisual;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.dws.aulavisual.users.UserManager;

import java.nio.file.Files;

@Controller
public class UserController {

    private final UserManager userManager;
    public UserController(UserManager userManager) {

        this.userManager = userManager;
    }

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @PostMapping("/login")
    public String userLogin(Model model, @RequestParam String username, @RequestParam String password, HttpServletResponse response) {


        if(userManager.login(username, password)){

            long userId = userManager.getUserId(username);
            // create a cookie
            Cookie cookie = new Cookie("userId", Long.toString(userId));

            //add cookie to response
            response.addCookie(cookie);
            model.addAttribute("userName", username);
                return "welcome";
        }else{

            return "redirect:/login/error";
        }
    }

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String password) {

        userManager.addUser(name, surname, username, password, 2);
        return "redirect:/login";
    }

    @GetMapping("/user_pfp{userId}")
    public ResponseEntity<Object> getUserPfp(@RequestParam String userId) {

        return null;
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {

        model.addAttribute("userName", "Test");
        model.addAttribute("userImage", "");
        return "profile";
    }
}
