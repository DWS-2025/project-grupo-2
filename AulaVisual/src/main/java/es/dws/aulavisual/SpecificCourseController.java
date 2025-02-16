package es.dws.aulavisual;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import es.dws.aulavisual.courses.CourseManager;


@Controller
public class SpecificCourseController {

    private final CourseManager courseManager;
    public SpecificCourseController(CourseManager courseManager) {

        this.courseManager = courseManager;
    }

    @GetMapping("/course")
    public String course(Model model) {

        return "courses/course";
    }
}