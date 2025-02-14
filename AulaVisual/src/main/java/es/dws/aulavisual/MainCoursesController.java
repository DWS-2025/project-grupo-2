package es.dws.aulavisual;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainCoursesController {

    @GetMapping("/coursesview")
    public String coursesview() {

        return "coursesview";
    }
}


