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
        if(user.getRole() == 0){

            model.addAttribute("admin", user.getRealName());
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
        if(user.getRole() == 0){

            model.addAttribute("courseName", course.getName());
            model.addAttribute("courseId", id);
            model.addAttribute("admin", user.getRealName());
            model.addAttribute("modules", course.getModules());
            return "courses/modules";
        }
        return "redirect:/";
    }

    @PostMapping("/admin/courses/{id}/modules/add")
    public String addModule(@RequestParam String name, @PathVariable long id, MultipartFile module) {

        if (!module.isEmpty()) {
            try {

                Course course = courseManager.getCourse(id);
                Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + id);
                Files.createDirectories(coursePath);
                Path modulePath = coursePath.resolve("module-" + course.getNumberModules() + "-" + name + ".md");
                module.transferTo(modulePath);
                String modulePathString = modulePath.toString();
                course.addModule(new Module(course.getNumberModules(), name, modulePathString));
            }catch (Exception e) {

                System.out.println("Error saving madule: " + e.getMessage());
            }
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
        if(user.getRole() == 0){

            Module module = course.getModule(id);
            if (module == null) {

                return "redirect:/admin/courses/{courseId}/modules/{id}";
            }

            model.addAttribute("courseId", course.getId());
            return "courses/modulePreview";
        }
        return "redirect:/";
    }

    @GetMapping("/course/{courseId}/module/{moduleId}/content")
    public ResponseEntity <Object> getModuleContent(@PathVariable long courseId, @PathVariable long moduleId, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return ResponseEntity.status(401).body("Unauthorized");
        }

        Course course = courseManager.getCourse(courseId);
        Module module = course.getModule(moduleId);
        if (module == null) {

            return ResponseEntity.status(404).body("Module not found");
        }
        try {

            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Path modulePath = coursePath.resolve("module-" + course.getNumberModules() + "-" + module.getName() + ".md");
            Resource content = new UrlResource(modulePath.toUri());
            if (!Files.exists(modulePath)) {

                return ResponseEntity.status(404).body("Module not found");
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown").body(content);
        }catch (Exception e) {

            return ResponseEntity.status(404).body("Module not found");
        }
    }

    @GetMapping("/addCourse")
    public String addCourse() {

        return "courses/addCourse";
    }

    @PostMapping("/addCourse")
    public String addCourse(@RequestParam String name, @RequestParam String description, @RequestParam long teacher) {

        List <Module> modules = new ArrayList <>();
        courseManager.createCourse(name, description, teacher, modules);
        return "redirect:/manageCourses";
    }

    @GetMapping("/course/{id}")
    public String getCourseById(@RequestParam long id) {

        return "courses/course";
    }
}