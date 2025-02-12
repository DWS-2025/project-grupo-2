package es.dws.aulavisual;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class MainViewController {

    @GetMapping("/")
    public String index() {

        return "index";
    }

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @PostMapping("/login")
    public String userLogin(Model model,@RequestParam String username, @RequestParam String password) {

        if (username.equals("admin") && password.equals("admin")) {
            model.addAttribute("userName", username);
            return "welcome";
        } else {
            return "redirect:/login/error";
        }
    }

    @GetMapping("/login/error")
    public String loginError() {

        return "../static/html/login-error";
    }
}
