package es.dws.aulavisual;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.NoSuchAlgorithmException;

import java.security.MessageDigest;

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

        String hashedPassword = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            hashedPassword = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }

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
