package es.dws.aulavisual;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserManager userManager;

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @PostMapping("/login")
    public String userLogin(Model model, @RequestParam String username, @RequestParam String password) {

        String hashedPassword = userManager.hashPassword(password);

        if(userManager.login(username, hashedPassword)){

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
