package es.dws.aulavisual;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.dws.aulavisual.users.UserManager;

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
    public String userLogin(Model model, @RequestParam String username, @RequestParam String password) {

        if(userManager.login(username, password)){

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
}
