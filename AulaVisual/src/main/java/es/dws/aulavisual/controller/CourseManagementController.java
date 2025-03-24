package es.dws.aulavisual.controller;

import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.UserMapper;
import es.dws.aulavisual.service.ModuleService;
import es.dws.aulavisual.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Module;
import es.dws.aulavisual.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;


@Controller
public class CourseManagementController {

    private final CourseService courseService;
    private final UserService userService;
    private final ModuleService moduleService;
    private final UserMapper userMapper;

    public CourseManagementController(CourseService courseService, UserService userService, ModuleService moduleService, UserMapper userMapper) {

        this.courseService = courseService;
        this.userService = userService;
        this.moduleService = moduleService;
        this.userMapper = userMapper;
    }

    @GetMapping("/admin/courses")
    public String manageCourses(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }

            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() == 0) {

                model.addAttribute("admin", user.realName());
                model.addAttribute("userId", Long.parseLong(userId));
                model.addAttribute("availableTeachers", userService.getAvaliableTeachers());
                List <Course> courses = courseService.getCourses();
                model.addAttribute("courses", courses);
                return "courses-management/manageCourses";
            }
            return "redirect:/";

        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/courses/{id}/modules")
    public String getModules(Model model, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try{

            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            Optional <Course> searchCourse = courseService.findById(id);
            if(searchCourse.isEmpty()) {

                model.addAttribute("message", "Curso no encontrado");
                return "error";
            }
            Course course = searchCourse.get();
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() == 0) {

                model.addAttribute("courseName", course.getName());
                model.addAttribute("courseId", id);
                model.addAttribute("userId", Long.parseLong(userId));
                model.addAttribute("admin", user.realName());
                model.addAttribute("modules", moduleService.getModulesByCourse(course));
                return "courses-management/modules";
            }
            return "redirect:/";

        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/courses/{courseId}/modules/{id}")
    public String getModule(@PathVariable long courseId, @PathVariable long id, Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try {

            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            Optional <Course> searchCourse = courseService.findById(courseId);
            if(searchCourse.isEmpty()) {

                model.addAttribute("message", "Curso no encontrado");
                return "error";
            }
            Course course = searchCourse.get();

            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() == 0) {

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
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/course/{courseId}/module/{id}/content")
    public ResponseEntity <Object> getModuleContent(@PathVariable long courseId, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try{
            if(userId.isEmpty()) {

                return ResponseEntity.status(401).body("Unauthorized");
            }

            UserDTO user = userService.findById(Long.parseLong(userId));
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

            if(user.role() == 0 || course.getTeacher().equals(user) || courseService.userIsInCourse(user, course)) {

                return moduleService.viewModule(module);
            }else{

                return ResponseEntity.status(403).body("Unauthorized");
            }
        } catch (NoSuchElementException e) {

            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/admin/courses/{courseId}/delete") //Cambiar a POST
    public String deleteCourse(@PathVariable long courseId, @CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        try {

            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() != 0) {

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
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/courses/{courseId}/module/{id}/delete") //Cambiar a POST
    public String deleteModule(Model model, @PathVariable long courseId, @PathVariable long id, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() != 0) {

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
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/courses/addCourse")
    public String addCourse(@CookieValue(value = "userId", defaultValue = "") String userId, Model model) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            if(userService.getAvaliableTeachers().isEmpty()) {

                model.addAttribute("message", "No hay profesores disponibles");
                return "error";
            }
            model.addAttribute("availableTeachers", userService.getAvaliableTeachers());
            model.addAttribute("userId", Long.parseLong(userId));
            return "courses-management/addCourse";
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/courses/{id}/assignTeacher")
    public String assignTeacher(@PathVariable long id, @RequestParam long teacherId, Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            Optional <Course> searchCourse = courseService.findById(id);
            if(searchCourse.isEmpty()) {

                model.addAttribute("message", "Curso no encontrado");
                return "error";
            }
            Course course = searchCourse.get();
            UserDTO teacherDTO = userService.findById(teacherId);
            if(teacherDTO.role() != 1) {

                model.addAttribute("message", "El usuario seleccionado no es un profesor");
                return "error";
            }

            course.setTeacher(userMapper.toDomain(teacherDTO));
            courseService.save(course);
            return "redirect:/admin/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/courses/addCourse")
    public String addCourse(Model model, @RequestParam String name, @RequestParam String description, @RequestParam long teacherId, MultipartFile image, @RequestParam String task, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try {
            if (userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if (user.role() != 0) {

                return "redirect:/";
            }
            UserDTO teacherDTO = userService.findById(teacherId);
            if (teacherDTO.role() != 1) {

                model.addAttribute("message", "El usuario seleccionado no es un profesor");
                return "error";
            }

            if (name.isEmpty() || description == null || description.isEmpty() || task.isEmpty()) {

                model.addAttribute("message", "Faltan campos por rellenar");
                return "error";
            }

            if (image != null && !image.isEmpty()) {

                if (teacherDTO.courseTeaching() != null) {

                    model.addAttribute("message", "El profesor ya tiene un curso asignado");
                    return "error";
                }
                Course course = new Course(name, description, userMapper.toDomain(teacherDTO), task, image);
                courseService.save(course);
            } else {

                model.addAttribute("message", "La imagen es obligatoria");
                return "error";
            }
            return "redirect:/admin/courses";
        } catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/courses/{courseId}/addModule")
    public String addModule(@CookieValue(value = "userId", defaultValue = "") String userId, Model model, @PathVariable long courseId) {

        try {

            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            Optional <Course> searchCourse = courseService.findById(courseId);
            if(searchCourse.isEmpty()) {

                model.addAttribute("message", "Curso no encontrado");
                return "error";
            }
            Course course = searchCourse.get();
            model.addAttribute("availablePositions", moduleService.getAvailablePositions(course));
            model.addAttribute("userId", Long.parseLong(userId));
            model.addAttribute("courseId", courseId);
            return "courses-management/addModule";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/courses/{courseId}/addModule")
    public String addModule(Model model, @PathVariable long courseId, @RequestParam String name, @RequestParam String position, MultipartFile module, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try{
            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            if(name.isEmpty() || module == null || module.isEmpty() || position.isEmpty()) {

                model.addAttribute("message", "Faltan campos por rellenar");
                return "error";
            }

            Optional <Course> searchCourse = courseService.findById(courseId);
            if(searchCourse.isEmpty()) {

                model.addAttribute("message", "Curso no encontrado");
                return "error";
            }
            Course course = searchCourse.get();
            int numPosition = Integer.parseInt(position);
            if(moduleService.positionExists(course, numPosition)) {

                model.addAttribute("message", "Ya existe un módulo en esa posición");
                return "error";
            }
            if (numPosition < 0) {

                model.addAttribute("message", "La posición no puede ser negativa");
                return "error";
            }
            moduleService.save(course, name, numPosition, module);
            return "redirect:/admin/courses/{courseId}/modules";
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
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

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findById(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            UserDTO student = userService.findById(studentId);
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
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
}