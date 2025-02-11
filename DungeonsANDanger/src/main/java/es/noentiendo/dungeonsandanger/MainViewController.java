package es.noentiendo.dungeonsandanger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    @GetMapping("/")
    public String mainPage(Model model){

        return "index";
    }
}
