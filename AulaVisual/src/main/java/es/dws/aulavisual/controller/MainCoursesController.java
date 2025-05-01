package es.dws.aulavisual.controller;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.ModuleSimpleDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.service.CourseService;
import java.util.NoSuchElementException;
import java.util.Optional;
import es.dws.aulavisual.service.ModuleService;
import es.dws.aulavisual.model.Module;
import es.dws.aulavisual.service.UserService;
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
    public String coursesview(Model model) {

        try {
            UserDTO user = (UserDTO) model.getAttribute("user");
            List <CourseDTO> userCourses = courseService.courseOfUser(user.id());
            List <CourseDTO> availableCourses = courseService.notCourseOfUser(user);


            model.addAttribute("userCourses", userCourses);
            model.addAttribute("availableCourses", availableCourses);
            return "courses-user/courses";
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/courses/{id}")
    public String singleCourse(@PathVariable long id, Model model) {

        try{

            CourseDTO courseDTO = courseService.findByIdDTO(id);
            return "redirect:/courses/" + id + "/module/" + moduleService.getFirstModuleByCourse(courseDTO.id());
        }catch (NoSuchElementException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }

    }

    @GetMapping("/courses/{id}/module/{moduleId}")
    public String usersCoursePanel(Model model, @PathVariable long id, @PathVariable long moduleId) {

        try {

            CourseDTO courseDTO = courseService.findByIdDTO(id);
            UserDTO user = (UserDTO) model.getAttribute("user");
            ModuleSimpleDTO module = moduleService.findById(moduleId);
            if(courseService.userIsInCourse(user.id(), courseDTO.id())) {

                List <ModuleSimpleDTO> modules = moduleService.getModulesByCourse(courseDTO);
                model.addAttribute("modules", modules);
                model.addAttribute("courseId", id);
                model.addAttribute("id", moduleId);
                return "courses-user/singleCourse";
            }else {

                model.addAttribute("message", "No tienes acceso a este curso");
                return "error";
            }
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
}


