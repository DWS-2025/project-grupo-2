package es.dws.aulavisual;

import es.dws.aulavisual.modules.ModuleService;
import es.dws.aulavisual.users.User;
import es.dws.aulavisual.users.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.modules.Module;
import es.dws.aulavisual.courses.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Controller
public class CourseManagementController {

    private final CourseService courseService;
    private final UserService userService;
    private final ModuleService moduleService;

    public CourseManagementController(CourseService courseService, UserService userService, ModuleService moduleService) {

        this.courseService = courseService;
        this.userService = userService;
        this.moduleService = moduleService;
    }

    @GetMapping("/admin/courses")
    public String manageCourses(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> userSearch = userService.findById(Long.parseLong(userId));
        if(userSearch.isEmpty()) {

            return "redirect:/login";
        }

        User user = userSearch.get();
        if(user.getRole() == 0) {

            model.addAttribute("admin", user.getRealName());
            model.addAttribute("userId", Long.parseLong(userId));
            List <Course> courses = courseService.getCourses();
            model.addAttribute("courses", courses);
            return "courses-management/manageCourses";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/courses/{id}/modules")
    public String getModules(Model model, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <Course> searchCourse = courseService.findById(id);
        if(searchCourse.isEmpty()) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() == 0) {

            model.addAttribute("courseName", course.getName());
            model.addAttribute("courseId", id);
            model.addAttribute("userId", Long.parseLong(userId));
            model.addAttribute("admin", user.getRealName());
            model.addAttribute("modules", moduleService.getModulesByCourse(course));
            return "courses-management/modules";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/courses/{courseId}/modules/{id}")
    public String getModule(@PathVariable long courseId, @PathVariable long id, Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();

        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() == 0) {

            Optional<Module> searchModule = moduleService.findById(id);
            if(searchModule.isEmpty()) {

                model.addAttribute("message", "Módulo no encontrado");
                return "error";
            }
            Module module = searchModule.get();
            model.addAttribute("courseId", courseId);
            model.addAttribute("module", module);
            return "courses-management/modulePreview";
        }
        return "redirect:/";
    }

    @GetMapping("/course/{courseId}/module/{id}/content")
    public ResponseEntity <Object> getModuleContent(@PathVariable long courseId, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            return ResponseEntity.status(404).body("User not found");
        }
        User user = searchUser.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()) {

            return ResponseEntity.status(404).body("Course not found");
        }
        Course course = searchCourse.get();

        Optional <Module> searchModule = moduleService.findById(id);
        if(searchModule.isEmpty()) {

            return ResponseEntity.status(404).body("Module not found");
        }
        Module module = searchModule.get();

        if(user.getRole() == 0 || course.getTeacherId() == user.getId() || courseService.userIsInCourse(user, course)) {

            return moduleService.viewModule(module);
        }else{

            return ResponseEntity.status(403).body("Unauthorized");
        }
    }

    @PostMapping("/admin/courses/{courseId}/delete") //Cambiar a POST
    public String deleteCourse(@PathVariable long courseId, @CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();
        courseService.deleteCourse(course); //Mirar submisssions
        return "redirect:/admin/courses";
    }

    @PostMapping("/admin/courses/{courseId}/module/{id}/delete") //Cambiar a POST
    public String deleteModule(Model model, @PathVariable long courseId, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();

        Optional <Module> searchModule = moduleService.findById(id);
        if(searchModule.isEmpty()) {

            model.addAttribute("message", "Módulo no encontrado");
            return "error";
        }
        Module module = searchModule.get();

        moduleService.delete(module);
        return "redirect:/admin/courses/{courseId}/modules";
    }

    @GetMapping("/admin/courses/addCourse")
    public String addCourse(@CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            return "redirect:/login";
        }
        User user = searchUser.get();
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        model.addAttribute("userId", Long.parseLong(userId));
        return "courses-management/addCourse";
    }

    @PostMapping("/admin/courses/addCourse")
    public String addCourse(Model model, @RequestParam String name, @RequestParam String description, @RequestParam long teacherId, MultipartFile image, @RequestParam String task, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        Optional <User> searchTecher = userService.findById(teacherId);
        if(searchTecher.isEmpty()) {

            model.addAttribute("message", "Usuario profesor no encontrado");
            return "error";
        }
        User techer = searchTecher.get();
        if (techer.getRole() != 1) {

            model.addAttribute("message", "El usuario seleccionado no es un profesor");
            return "error";
        }
        if(name.isEmpty() || description == null || description.isEmpty() || task.isEmpty()) {

            model.addAttribute("message", "Faltan campos por rellenar");
            return "error";
        }

        if(image != null && !image.isEmpty()) {

            Course course = new Course(name, description, teacherId, task, image);
            courseService.save(course);
        }else{

            model.addAttribute("message", "La imagen es obligatoria");
            return "error";
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/{courseId}/addModule")
    public String addModule(@CookieValue(value = "userId", defaultValue = "") String userId, Model model, @PathVariable long courseId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        model.addAttribute("userId", Long.parseLong(userId));
        model.addAttribute("courseId", courseId);
        return "courses-management/addModule";
    }

    @PostMapping("/admin/courses/{courseId}/addModule")
    public String addModule(Model model, @PathVariable long courseId, @RequestParam String name, MultipartFile module, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        if(name.isEmpty() || module == null || module.isEmpty()) {

            model.addAttribute("message", "Faltan campos por rellenar");
            return "error";
        }

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();
        moduleService.save(course, name, module);
        return "redirect:/admin/courses/{courseId}/modules";
    }

    @GetMapping("/courses/{courseId}/getImage")
    public ResponseEntity <Object> getImage(@PathVariable Long courseId) {

        Optional<Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()) {

            return ResponseEntity.notFound().build();
        }
        Course course = searchCourse.get();

        ResponseEntity <Object> response = courseService.loadImage(course);

        return Objects.requireNonNullElseGet(response, () -> ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/courses/{courseId}/addStudent")
    public String addStudent(@PathVariable long courseId, @RequestParam long studentId, Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        User user = searchUser.get();
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        Optional <User> seatchStudent = userService.findById(studentId);
        if(seatchStudent.isEmpty()) {

            model.addAttribute("message", "Estudiante no encontrado");
            return "error";
        }
        User student = seatchStudent.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();
        if(courseService.userIsInCourse(student, course)) {

            model.addAttribute("message", "El estudiante ya está en el curso");
            return "error";
        }
        courseService.addUserToCourse(course, student);

        return "redirect:/admin/courses/{courseId}/";
    }
}