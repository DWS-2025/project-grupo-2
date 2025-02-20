package es.dws.aulavisual;

import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.CourseManager;
import es.dws.aulavisual.users.User;
import es.dws.aulavisual.users.UserManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainCoursesController {

    private final CourseManager courseManager;
    private final UserManager userManager;

    public MainCoursesController(CourseManager courseManager, UserManager userManager) {
        this.courseManager = courseManager;
        this.userManager = userManager;
    }

    @GetMapping("/coursesview")
    public String coursesview(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null){
            return "redirect:/";
        }
        List <Course> courses = courseManager.getCourses();

        List <Long> usersInCourse;
        for (Course course : courses) {

            usersInCourse = course.getUserIds();
            if(usersInCourse.contains(Long.parseLong(userId))){


            }
        }

        model.addAttribute("courses", courses);
        return "courses/coursesview";
    }
}


