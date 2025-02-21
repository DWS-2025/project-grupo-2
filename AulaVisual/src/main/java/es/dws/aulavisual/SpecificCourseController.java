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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Controller
public class SpecificCourseController {

    private final CourseManager courseManager;
    private final UserManager userManager;

    public SpecificCourseController(CourseManager courseManager, UserManager userManager) {

        this.courseManager = courseManager;
        this.userManager = userManager;
    }

    @GetMapping("/admin/courses")
    public String manageCourses(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() == 0) {

            model.addAttribute("admin", user.getRealName());
            model.addAttribute("userId", Long.parseLong(userId));
            List <Course> courses = courseManager.getCourses();
            model.addAttribute("courses", courses);
            return "courses/manageCourses";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/courses/{id}/modules")
    public String getModules(Model model, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Course course = courseManager.getCourse(id);
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() == 0) {

            model.addAttribute("courseName", course.getName());
            model.addAttribute("courseId", id);
            model.addAttribute("userId", Long.parseLong(userId));
            model.addAttribute("admin", user.getRealName());
            model.addAttribute("modules", course.getModules());
            return "courses/modules";
        }
        return "redirect:/";
    }

    @PostMapping("/admin/courses/{id}/modules/add")
    public String addModule(@RequestParam String name, @PathVariable long id, MultipartFile module) {

        if(!module.isEmpty()) {
            courseManager.addModule(id, name, module);
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/{courseId}/modules/{id}")
    public String getModule(@PathVariable long courseId, @PathVariable long id, Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Course course = courseManager.getCourse(courseId);
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() == 0) {

            Module module = course.getModuleById(id);
            if(module == null) {

                return "redirect:/admin/courses/{courseId}/modules/{id}";
            }

            model.addAttribute("courseId", course.getId());
            model.addAttribute("userId", Long.parseLong(userId));
            return "courses/test";
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

    @GetMapping("/course/{courseId}/delete")
    public String deleteCourse(@PathVariable long courseId, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        courseManager.removeCourse(courseId);
        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/{courseId}/module/{id}/delete")
    public String deleteModule(@PathVariable long courseId, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        Course course = courseManager.getCourse(courseId);
        Module module = course.getModuleById(id);
        if(module == null) {

            return "redirect:/admin/courses/{courseId}/modules";
        }
        courseManager.removeModule(courseId, id);
        return "redirect:/admin/courses/{courseId}/modules";
    }

    @GetMapping("/admin/courses/addCourse")
    public String addCourse() {

        return "courses/addCourse";
    }

    @PostMapping("/admin/courses/addCourse")
    public String addCourse(@RequestParam String name, @RequestParam String description, @RequestParam long teacherId, MultipartFile image, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() != 0) {

            return "redirect:/";
        }
        List <Module> modules = new ArrayList <>();
        courseManager.createCourse(name, description, teacherId, modules);
        if(image != null && !image.isEmpty()) {

            courseManager.addImage(image);
        }
        return "redirect:/admin/courses";
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

    @PostMapping("/addCourse")
    public String addCourse(@RequestParam String name, @RequestParam String description, @RequestParam long teacher) {

        List <Module> modules = new ArrayList <>();
        courseManager.createCourse(name, description, teacher, modules);
        return "redirect:/manageCourses";
    }
}