package es.dws.aulavisual;

import org.springframework.ui.Model;
import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.Module;
import es.dws.aulavisual.courses.CourseManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class SpecificCourseController {

    private final CourseManager courseManager;

    public SpecificCourseController(CourseManager courseManager) {

        this.courseManager = courseManager;
    }

    @GetMapping("/admin/courses")
    public String manageCourses(Model model) {

        return "courses/manageCourses";
    }

    @PostMapping("/admin/courses")
    public String createCourse(@RequestParam String name, @RequestParam String description, @RequestParam long teacher) {

        List <Module> modules = new ArrayList <>();
        courseManager.createCourse(name, description, teacher, modules);
        return "redirect:/admin/courses";
    }

    @PostMapping("/admin/courses/{id}/modules")
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
    public String getCourseById(@RequestParam long id, Model model) {

        return "courses/course";
    }
}