package es.dws.aulavisual.controller;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.ModuleSimpleDTO;
import es.dws.aulavisual.DTO.UserDTO;
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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
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

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }

            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() == 0) {

                model.addAttribute("admin", user.realName());
                model.addAttribute("userId", Long.parseLong(userId));
                model.addAttribute("availableTeachers", userService.getAvaliableTeachers());
                List <CourseDTO> courses = courseService.getCourses();
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
            CourseDTO courseDTO = courseService.findByIdDTO(id);
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() == 0) {

                model.addAttribute("courseName", courseDTO.name());
                model.addAttribute("courseId", id);
                model.addAttribute("userId", Long.parseLong(userId));
                model.addAttribute("admin", user.realName());
                model.addAttribute("modules", moduleService.getModulesByCourse(courseDTO));
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
            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() == 0) {

                ModuleSimpleDTO module = moduleService.findById(id);
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

            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            CourseDTO course = courseService.findByIdDTO(courseId);

            ModuleSimpleDTO module = moduleService.findById(id);

            if(user.role() == 0 || course.teacher().id() == user.id() || courseService.userIsInCourse(user, course)) {

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
//            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
//            if(user.role() != 0) {
//
//                return "redirect:/";
//            }
            //CourseDTO course = courseService.findByIdDTO(courseId);
            courseService.deleteCourse(courseId); //Mirar submisssions
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
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            //CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            ModuleSimpleDTO module = moduleService.findById(id);

            moduleService.delete(module, courseId);
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
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
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
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            CourseDTO courseDTO = courseService.findByIdDTO(id);
            UserDTO teacherDTO = userService.findByIdDTO(teacherId);
            if(teacherDTO.role() != 1) {

                model.addAttribute("message", "El usuario seleccionado no es un profesor");
                return "error";
            }

            courseService.saveDTO(courseDTO);
            courseService.assignTeacher(teacherDTO, courseDTO);
            return "redirect:/admin/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/courses/addCourse")
    public String addCourse(Model model, CourseDTO courseDTO, @RequestParam long teacherId, MultipartFile imageCourse, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try {
            if (userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if (user.role() != 0) {

                return "redirect:/";
            }
            UserDTO teacherDTO = userService.findByIdDTO(teacherId);
            if (teacherDTO.role() != 1) {

                model.addAttribute("message", "El usuario seleccionado no es un profesor");
                return "error";
            }

            if (courseDTO.name().isEmpty() || courseDTO.description() == null || courseDTO.description().isEmpty() || courseDTO.task().isEmpty()) {

                model.addAttribute("message", "Faltan campos por rellenar");
                return "error";
            }

            if (imageCourse != null && !imageCourse.isEmpty()) {

                if (teacherDTO.courseTeaching() != null) {

                    model.addAttribute("message", "El profesor ya tiene un curso asignado");
                    return "error";
                }
                courseDTO = courseService.saveDTO(courseDTO);
                courseService.assignTeacher(teacherDTO, courseDTO);
                URI location = URI.create(String.format("/course/" + courseDTO.id() + "/image/"));
                courseService.uploadImage(courseDTO.id(), location, imageCourse.getInputStream(), imageCourse.getSize());
            } else {

                model.addAttribute("message", "La imagen es obligatoria");
                return "error";
            }
            return "redirect:/admin/courses";
        } catch (NoSuchElementException | IOException e) {

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
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            model.addAttribute("availablePositions", moduleService.getAvailablePositions(courseDTO));
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
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            if(name.isEmpty() || module == null || module.isEmpty() || position.isEmpty()) {

                model.addAttribute("message", "Faltan campos por rellenar");
                return "error";
            }

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            int numPosition = Integer.parseInt(position);
            if(moduleService.positionExists(courseDTO, numPosition)) {

                model.addAttribute("message", "Ya existe un módulo en esa posición");
                return "error";
            }
            if (numPosition < 0) {

                model.addAttribute("message", "La posición no puede ser negativa");
                return "error";
            }
            moduleService.save(courseDTO, name, numPosition, module);
            return "redirect:/admin/courses/{courseId}/modules";
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/courses/{courseId}/getImage")
    public ResponseEntity <Object> getImage(@PathVariable Long courseId) {

        CourseDTO course = courseService.findByIdDTO(courseId);

        ResponseEntity <Object> response = courseService.loadImage(course);

        return Objects.requireNonNullElseGet(response, () -> ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/courses/{courseId}/addStudent")
    public String addStudent(@PathVariable long courseId, @RequestParam long studentId, Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));
            if(user.role() != 0) {

                return "redirect:/";
            }
            UserDTO student = userService.findByIdDTO(studentId);
            CourseDTO course = courseService.findByIdDTO(courseId);
            if(courseService.userIsInCourse(student, course)) {

                model.addAttribute("message", "El estudiante ya está en el curso");
                return "error";
            }
            courseService.addUserToCourse(course, student);

            return "redirect:/admin/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
}