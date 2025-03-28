package es.dws.aulavisual.controller;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.service.CourseService;
import es.dws.aulavisual.service.SubmissionService;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class SubmissionController {

    private final UserService userService;
    private final SubmissionService submissionService;
    private final CourseService courseService;

    public SubmissionController(UserService userService, SubmissionService submissionService, CourseService courseService) {
        this.userService = userService;
        this.submissionService = submissionService;
        this.courseService = courseService;
    }

    @GetMapping("/courses/{courseId}/submission")
    public String seeCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId) {

        try {
            if(userId.isEmpty()){
                return "redirect:/login";
            }
            UserDTO userDTO = userService.findByIdDTO(Long.parseLong(userId));

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);

            if(userDTO.role() == 1){

                return "redirect:/courses/" + courseId + "/grade";
            }

            if(userDTO.role() == 2){

                if(courseService.userIsInCourse(userDTO, courseDTO)){

                    model.addAttribute("courseId", courseId);
                    model.addAttribute("courseName", courseDTO.name());
                    if(!submissionService.userMadeSubmission(userDTO, courseDTO)){

                        model.addAttribute("submitted", false);
                        model.addAttribute("task", courseDTO.task());
                    }else {

                        SubmissionDTO submission = submissionService.findByUserAndCourse(userDTO, courseDTO);
                        model.addAttribute("submitted", true);
                        if(submission.graded()) {

                            model.addAttribute("grade", submission.grade());
                        }else{

                            model.addAttribute("grade", "No Disponible");
                        }
                    }
                }else {

                    model.addAttribute("message", "No tienes acceso a este curso");
                    return "error";
                }

                return "courses-user/submission";
            }
            return "redirect:/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/courses/{courseId}/grade")
    public String gradeCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId) {

        try {
            if(userId.isEmpty()){
                return "redirect:/login";
            }
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);

            if(user.role() == 1){

                List<SubmissionDTO> submissions = submissionService.getSubmissions(courseDTO, false);
                List<SubmissionDTO> graded = submissionService.getSubmissions(courseDTO, true);
                model.addAttribute("gradedSubmissions", graded);
                model.addAttribute("submissions", submissions);
                model.addAttribute("courseId", courseId);
                model.addAttribute("courseName", courseDTO.name());
                return "courses-user/submissionsGrade";
            }
            return "redirect:/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/courses/{courseId}/grade/{studentId}")
    public String gradeCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId, @RequestParam float grade) {

        try {
            if(userId.isEmpty()){
                return "redirect:/login";
            }
            UserDTO userDTO = userService.findByIdDTO(Long.parseLong(userId));

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);

            UserDTO studentDTO = userService.findByIdDTO(studentId);

            if(userDTO.role() == 1 && courseDTO.teacher().id() == userDTO.id()){

                if(courseService.userIsInCourse(studentDTO, courseDTO)){

                    if(submissionService.userMadeSubmission(studentDTO, courseDTO)){

                        submissionService.gradeSubmission(courseDTO, studentDTO, grade);
                        return "redirect:/courses/" + courseId + "/grade";
                    }else{

                        model.addAttribute("message", "Estudiante no ha entregado la tarea");
                        return "error";
                    }
                }else{

                    model.addAttribute("message", "Estudiante no cursando el curso");
                    return "error";
                }
            }
            return "redirect:/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/courses/{courseId}/submission/download/{studentId}")
    public ResponseEntity<Object> downloadCourseSubmission(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId) {

        try {
            if(userId.isEmpty()){
                return ResponseEntity.status(401).body("Unauthorized");
            }
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);

            UserDTO student = userService.findByIdDTO(studentId);
            if(user.role() == 1 && courseDTO.teacher().id() == user.id()){

                if(courseService.userIsInCourse(student, courseDTO)) {

                    if(submissionService.userMadeSubmission(student, courseDTO)) {

                        return submissionService.getSubmission(courseDTO, student);
                    }else {

                        return ResponseEntity.status(404).body("Submission not found");
                    }
                }else{

                    return ResponseEntity.status(404).body("User is not inm course");
                }
            }
            return ResponseEntity.status(401).body("Unauthorized");
        }catch (NoSuchElementException e){

            return ResponseEntity.status(404).body("Error");
        }
    }

    @PostMapping("/courses/{courseId}/submission")
    public String submitCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, MultipartFile submission) {

        try {
            if(userId.isEmpty()){
                return "redirect:/login";
            }
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            if(submission.isEmpty()){

                model.addAttribute("message", "No se ha seleccionado un archivo");
                return "error";
            }
            if(courseService.userIsInCourse(user, courseDTO)) {

                if(!submissionService.userMadeSubmission(user, courseDTO)) {

                    submissionService.save(courseDTO, user, submission);
                }
            }
            return "redirect:/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/courses/{courseId}/draft/{studentId}")
    public String deleteSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId) {

        try {
            if(userId.isEmpty()){
                return "redirect:/login";
            }
            UserDTO user = userService.findByIdDTO(Long.parseLong(userId));

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            UserDTO student = userService.findByIdDTO(studentId);

            if(courseService.userIsInCourse(student, courseDTO)) {

                if(submissionService.userMadeSubmission(student, courseDTO)) {

                    if(courseDTO.teacher().id().equals(user.id())) {

                        submissionService.deleteSubmission(student, courseDTO);
                        return "redirect:/courses/" + courseId + "/grade";
                    }
                }
            }
            return "redirect:/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
}
