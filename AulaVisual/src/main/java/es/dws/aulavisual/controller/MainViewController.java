package es.dws.aulavisual.controller;

import es.dws.aulavisual.model.User;
import es.dws.aulavisual.service.UserService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Optional;

@Controller
public class MainViewController {

    private final UserService userService;

    public MainViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(@CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        if (!userId.isEmpty()){
            Optional <User> searchUser = userService.findById(Long.parseLong(userId));
            if(searchUser.isEmpty()) {
                model.addAttribute("user", false);
                return "index";
            }
            User user = searchUser.get();
            model.addAttribute("user", true);
            model.addAttribute("userId", Long.parseLong(userId));
            model.addAttribute("userName", user.getUserName());
        }else{

            model.addAttribute("user", false);
        }
        return "index";
    }
}
