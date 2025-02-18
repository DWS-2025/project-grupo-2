package es.dws.aulavisual;

import es.dws.aulavisual.courses.Course;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainCoursesController {

    @GetMapping("/coursesview")
    public String coursesview(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        Course course = new Course(0, "test", "test1", null);
        Course course2 = new Course(0, "test2", "test2", null);
        Course course3 = new Course(0, "test3", "test3", null);
        List <Course> courses = new ArrayList <>();
        courses.add(course);
        courses.add(course2);
        courses.add(course3);
        model.addAttribute("courses", courses);
        return "courses/coursesview";
    }
}


