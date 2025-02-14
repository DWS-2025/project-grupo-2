package es.dws.aulavisual;

import org.springframework.ui.Model;
import es.dws.aulavisual.courses.CourseManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class SpecificCourseController {
    private final CourseManager courseManager;
    public SpecificCourseController(CourseManager courseManager) {

        this.courseManager = courseManager;
    }

    @GetMapping("/course")
    public String course(Model model) {

        model.addAttribute("name", courseManager.getCourses());
        return "course";
    }

    @PostMapping ("/course")
    public String showCourse(Model model) {

        return "course";
    }
}