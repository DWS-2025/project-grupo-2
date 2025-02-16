package es.dws.aulavisual;

import es.dws.aulavisual.courses.Course;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import es.dws.aulavisual.courses.CourseManager;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SpecificCourseController {

    private final CourseManager courseManager;

    public SpecificCourseController(CourseManager courseManager) {

        this.courseManager = courseManager;
    }

    @GetMapping("/course/{id}")
    public String getCourseById(@RequestParam long id, Model model) {

        Course course = courseManager.getCourse(id);
        model.addAttribute("course", course);
        return "courses/course";
    }
}