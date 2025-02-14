package es.dws.aulavisual;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.dws.aulavisual.users.UserManager;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("userId", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @PostMapping("/profile/update")
    public String updateUser(@RequestParam String username, @RequestParam String prevPassword, @RequestParam String newPassword, MultipartFile image, @CookieValue("userId") String userId) {

        if(!username.isEmpty()){

            if(userManager.updateUsername(Long.parseLong(userId), username)){

                return "redirect:/login";
            }else{

                return "redirect:/profile/error";
            }
        }

        if(!newPassword.isEmpty()){

            if(userManager.updatePassword(Long.parseLong(userId), prevPassword, newPassword)){

                return "redirect:/login";
            }else{

                return "redirect:/profile/error";
            }
        }

        if(!image.isEmpty()){


        }

        return "/profile";
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
