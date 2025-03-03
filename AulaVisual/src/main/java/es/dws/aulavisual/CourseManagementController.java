package es.dws.aulavisual;

import es.dws.aulavisual.users.User;
import es.dws.aulavisual.users.UserManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.Module;
import es.dws.aulavisual.courses.CourseManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Controller
public class CourseManagementController {

    private final CourseManager courseManager;
    private final UserManager userManager;

    public CourseManagementController(CourseManager courseManager, UserManager userManager) {

        this.courseManager = courseManager;
        this.userManager = userManager;
    }

    @GetMapping("/admin/courses")
    public String manageCourses(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(user.getRole() == 0) {

            model.addAttribute("admin", user.getRealName());
            model.addAttribute("userId", Long.parseLong(userId));
            List <Course> courses = courseManager.getCourses();
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
        Course course = courseManager.getCourse(id);
        if(course == null) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(user.getRole() == 0) {

            model.addAttribute("courseName", course.getName());
            model.addAttribute("courseId", id);
            model.addAttribute("userId", Long.parseLong(userId));
            model.addAttribute("admin", user.getRealName());
            model.addAttribute("modules", course.getModules());
            return "courses-management/modules";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/courses/{courseId}/modules/{id}")
    public String getModule(@PathVariable long courseId, @PathVariable long id, Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Course course = courseManager.getCourse(courseId);
        if(course == null) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(user.getRole() == 0) {

            Module module = course.getModuleById(id);
            if(module == null) {

                model.addAttribute("message", "Módulo no encontrado");
                return "error";
            }

            model.addAttribute("courseId", course.getId());
            model.addAttribute("userId", Long.parseLong(userId));
            return "courses-management/modulePreview";
        }
        return "redirect:/";
    }

    @GetMapping("/course/{courseId}/module/{id}/content")
    public ResponseEntity <Object> getModuleContent(@PathVariable long courseId, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return ResponseEntity.status(401).body("Unauthorized");
        }

        Course course = courseManager.getCourse(courseId);
        Module module = course.getModuleById(id);
        if(module == null) {

            return ResponseEntity.status(404).body("Module not found1");
        }
        return courseManager.viewCourse(courseId, id);
    }

    @GetMapping("/admin/courses/{courseId}/delete")
    public String deleteCourse(@PathVariable long courseId, @CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        if(courseManager.removeCourse(courseId) == null) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/{courseId}/module/{id}/delete")
    public String deleteModule(Model model, @PathVariable long courseId, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        Course course = courseManager.getCourse(courseId);
        if(course == null) {

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Module module = course.getModuleById(id);
        if(module == null) {

            model.addAttribute("message", "Módulo no encontrado");
            return "error";
        }
        courseManager.removeModule(courseId, id);
        return "redirect:/admin/courses/{courseId}/modules";
    }

    @GetMapping("/admin/courses/addCourse")
    public String addCourse(@CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
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
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        User techer = userManager.getUser(teacherId);
        if(techer == null) {

            model.addAttribute("message", "Profesor no encontrado");
            return "error";
        }
        if (techer.getRole() != 1) {

            return "redirect:/";    //Should inform the user that a teacher is required
        }
        if(name.isEmpty() || description == null || description.isEmpty() || task.isEmpty()) {

            model.addAttribute("message", "Faltan campos por rellenar");
            return "error";
        }
        List <Module> modules = new ArrayList <>();
        courseManager.createCourse(name, description, teacherId, modules, task);
        if(image != null && !image.isEmpty()) {

            courseManager.addImage(image);
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/{courseId}/addModule")
    public String addModule(@CookieValue(value = "userId", defaultValue = "") String userId, Model model, @PathVariable long courseId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
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
        User user = userManager.getUser(Long.parseLong(userId));
        if(user == null) {

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        if(name.isEmpty() || module == null || module.isEmpty()) {

            model.addAttribute("message", "Faltan campos por rellenar");
            return "error";
        }
        if(courseManager.addModule(courseId, name, module)){

            return "redirect:/admin/courses/{courseId}/modules";
        }else {

            model.addAttribute("message", "Error al añadir el módulo");
            return "error";
        }
    }

    @GetMapping("/courses/{courseId}/getImage")
    public ResponseEntity <Resource> getImage(@PathVariable Long courseId) {

        try {
            Path path = courseManager.getImage(courseId);
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(resource);
        } catch (Exception e) {

            return ResponseEntity.notFound().build();
        }
    }
}