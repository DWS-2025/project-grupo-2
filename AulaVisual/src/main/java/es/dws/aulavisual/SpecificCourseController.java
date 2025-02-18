package es.dws.aulavisual;

import org.springframework.ui.Model;
import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.CourseManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SpecificCourseController {

    private final CourseManager courseManager;

    public SpecificCourseController(CourseManager courseManager) {

        this.courseManager = courseManager;
    }

    @GetMapping("/manageCourseS")
    public String manageCourses(Model model) {

        model.addAttribute("courses", courseManager.getCourses());
        return "courses/manageCourses";
    }

    @GetMapping("/addCourse")
    public String addCourse() {

        return "courses/addCourse";
    }

    @GetMapping("/course/{id}")
    public String getCourseById(@RequestParam long id, Model model) {

        Course course = courseManager.getCourse(id);
        model.addAttribute("course", course);
        return "courses/course";
    }
}