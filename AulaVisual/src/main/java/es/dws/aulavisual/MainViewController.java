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


    @GetMapping("/login/error")
    public String loginError() {

        return "../static/html/login-error";
    }
}
