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
    public String seeCourseSubmission(Model model, @PathVariable long courseId) {

        try {

            UserDTO userDTO = (UserDTO) model.getAttribute("user");
            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            if(userDTO.role().equals("TEACHER")){

                return "redirect:/teacher/courses/" + courseId + "/submissions";
            }


            if(courseService.userIsInCourse(userDTO.id(), courseDTO.id())){

                model.addAttribute("courseId", courseId);
                model.addAttribute("courseName", courseDTO.name());
                if(!submissionService.userMadeSubmission(userDTO.id(), courseDTO.id())){

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

        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/teacher/courses/{courseId}/submissions")
    public String gradeCourseSubmission(Model model, @PathVariable long courseId) {

        try {

            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            List<SubmissionDTO> submissions = submissionService.getSubmissions(courseDTO, false);
            List<SubmissionDTO> graded = submissionService.getSubmissions(courseDTO, true);
            model.addAttribute("gradedSubmissions", graded);
            model.addAttribute("submissions", submissions);
            model.addAttribute("courseId", courseId);
            model.addAttribute("courseName", courseDTO.name());
            return "courses-user/submissionsGrade";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/teacher/courses/{courseId}/grade/{studentId}")
    public String gradeCourseSubmission(Model model, @PathVariable long courseId, @PathVariable long studentId, @RequestParam float grade) {

        try {

            UserDTO teacher = (UserDTO) model.getAttribute("user");
            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            UserDTO studentDTO = userService.findByIdDTO(studentId);

            if(courseDTO.teacher().id().equals(teacher.id())){

                if(courseService.userIsInCourse(studentDTO.id(), courseDTO.id())){

                    if(submissionService.userMadeSubmission(studentDTO.id(), courseDTO.id())){

                        submissionService.gradeSubmission(courseDTO, studentDTO, grade);
                        return "redirect:/teacher/courses/" + courseId + "/submissions";
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

    @GetMapping("/teacher/courses/{courseId}/submission/download/{studentId}")
    public ResponseEntity<Object> downloadCourseSubmission(@PathVariable long courseId, @PathVariable long studentId, Model model) {

        try {

            UserDTO user = (UserDTO) model.getAttribute("user");
            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            UserDTO student = userService.findByIdDTO(studentId);

            if(courseDTO.teacher().id().equals(user.id())){

                if(courseService.userIsInCourse(student.id(), courseDTO.id())) {

                    if(submissionService.userMadeSubmission(student.id(), courseDTO.id())) {

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
    public String submitCourseSubmission(Model model, @PathVariable long courseId, MultipartFile submission) {

        try {

            UserDTO user = (UserDTO) model.getAttribute("user");
            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            if(submission.isEmpty()){

                model.addAttribute("message", "No se ha seleccionado un archivo");
                return "error";
            }
            submissionService.save(courseDTO, user, submission);
            return "redirect:/courses";
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/teacher/courses/{courseId}/draft/{studentId}")
    public String deleteSubmission(Model model, @PathVariable long courseId, @PathVariable long studentId) {

        try {

            UserDTO user = (UserDTO) model.getAttribute("user");
            CourseDTO courseDTO = courseService.findByIdDTO(courseId);
            UserDTO student = userService.findByIdDTO(studentId);

            if(courseService.userIsInCourse(student.id(), courseDTO.id())) {

                if(submissionService.userMadeSubmission(student.id(), courseDTO.id())) {

                    if(courseDTO.teacher().id().equals(user.id())) {

                        submissionService.deleteSubmission(student, courseDTO);
                        return "redirect:/teacher/courses/" + courseId + "/submissions";
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
