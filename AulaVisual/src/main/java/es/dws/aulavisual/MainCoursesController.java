package es.dws.aulavisual;

import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.CourseManager;
import es.dws.aulavisual.users.User;
import es.dws.aulavisual.users.UserManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainCoursesController {

    private final CourseManager courseManager;
    private final UserManager userManager;

    public MainCoursesController(CourseManager courseManager, UserManager userManager) {
        this.courseManager = courseManager;
        this.userManager = userManager;
    }

    @GetMapping("/courses")
    public String coursesview(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null){
            return "redirect:/";
        }
        List <Course> courses = courseManager.getCourses();
        List <Course> userCourses = new ArrayList<>();
        List <Course> availableCourses = new ArrayList<>();

        for(Course course : courses){

            if(courseManager.userInCourse(course.getId(), Long.parseLong(userId))){

                userCourses.add(course);
            }else{

                availableCourses.add(course);
            }
        }

        model.addAttribute("userCourses", userCourses);
        model.addAttribute("availableCourses", availableCourses);
        return "courses_users/coursesview";
    }



    @GetMapping("/courses/{id}/module/{moduleId}")
    public String usersCoursePanel(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id, @PathVariable long moduleId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        Course course = courseManager.getCourse(id);
        if(courseManager.userInCourse(id, Long.parseLong(userId))) {

            model.addAttribute("modules", course.getModules());
            model.addAttribute("courseId", id);
            model.addAttribute("id", moduleId);
            return "courses_users/usersCoursePanel";
        }

        return "redirect:/courses";
    }
}


