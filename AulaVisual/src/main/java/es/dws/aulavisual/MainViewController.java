package es.dws.aulavisual;

import es.dws.aulavisual.users.User;
import es.dws.aulavisual.users.UserService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    private final UserService userService;

    public MainViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(@CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        if (!userId.isEmpty()){
            model.addAttribute("user", true);
            model.addAttribute("userId", Long.parseLong(userId));
            User user = userService.getUser(Long.parseLong(userId));
            model.addAttribute("userName", user.getUserName());
        }else{

            model.addAttribute("user", false);
        }
        return "index";
    }
}
