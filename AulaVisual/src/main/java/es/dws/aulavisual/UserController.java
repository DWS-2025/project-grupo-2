package es.dws.aulavisual;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import users.UserManager;

@Controller
public class UserController {

    @Autowired
    private UserManager userManager;

    @GetMapping("/login"){


    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String password) {

        return "register";
    }
}
