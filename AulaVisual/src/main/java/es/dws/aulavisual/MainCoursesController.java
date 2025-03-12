package es.dws.aulavisual;

import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.CourseService;
import java.util.Optional;

import es.dws.aulavisual.modules.ModuleService;
import es.dws.aulavisual.modules.Module;
import es.dws.aulavisual.users.User;
import es.dws.aulavisual.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class MainCoursesController {

    private final CourseService courseService;
    private final UserService userService;
    private final ModuleService moduleService;

    public MainCoursesController(CourseService courseService, UserService userService, ModuleService moduleService) {
        this.courseService = courseService;
        this.userService = userService;
        this.moduleService = moduleService;
    }

    @GetMapping("/courses")
    public String coursesview(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> serachUser = userService.findById(Long.parseLong(userId));
        if(serachUser.isEmpty()) {

            return "redirect:/login";
        }
        User user = serachUser.get();
        List <Course> userCourses = courseService.courseOfUser(user);
        List <Course> availableCourses = courseService.notCourseOfUser(user);


        model.addAttribute("user", user);
        model.addAttribute("userId", Long.parseLong(userId));
        model.addAttribute("userCourses", userCourses);
        model.addAttribute("availableCourses", availableCourses);
        return "courses-user/courses";
    }



    @GetMapping("/courses/{id}/module/{moduleId}")
    public String usersCoursePanel(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id, @PathVariable long moduleId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <Course> searchCourse = courseService.findById(id);
        if(searchCourse.isEmpty()) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();
        Optional<User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        Optional <Module> searchModule = moduleService.findById(moduleId);
        if(searchModule.isEmpty()) {

            model.addAttribute("message", "Módulo no encontrado");
            return "error";
        }
        if(courseService.userIsInCourse(user, course)) {

            model.addAttribute("modules", moduleService.findById(moduleId));
            model.addAttribute("courseId", id);
            model.addAttribute("id", moduleId);
            return "courses-user/singleCourse";
        }else {

            model.addAttribute("message", "No tienes acceso a este curso");
            return "error";
        }
    }
}


