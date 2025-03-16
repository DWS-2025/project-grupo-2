package es.dws.aulavisual.controller;

import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.service.CourseService;
import es.dws.aulavisual.model.Submission;
import es.dws.aulavisual.service.SubmissionService;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import es.dws.aulavisual.model.User;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Optional;

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

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()){

            return "redirect:/login";
        }
        User user = searchUser.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();

        if(user.getRole() == 1){

            return "redirect:/courses/" + courseId + "/grade";
        }

        if(user.getRole() == 2){

            if(courseService.userIsInCourse(user, course)){

                model.addAttribute("courseId", courseId);
                model.addAttribute("courseName", course.getName());
                if(!submissionService.userMadeSubmission(user, course)){

                    model.addAttribute("submitted", false);
                    model.addAttribute("task", course.getTask());
                }else {

                    Submission submission = submissionService.findByUserAndCourse(user, course).get();
                    model.addAttribute("submitted", true);
                    if(submission.isGraded()) {

                        model.addAttribute("grade", submission.getGrade());
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
    }

    @GetMapping("/courses/{courseId}/grade")
    public String gradeCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()){

            return "redirect:/login";
        }
        User user = searchUser.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();

        if(user.getRole() == 1){

            List<Submission> submissions = submissionService.getSubmissions(course, false);
            List<Submission> graded = submissionService.getSubmissions(course, true);
            model.addAttribute("gradedSubmissions", graded);
            model.addAttribute("submissions", submissions);
            model.addAttribute("courseId", courseId);
            model.addAttribute("courseName", course.getName());
            return "courses-user/submissionsGrade";
        }
        return "redirect:/courses";
    }

    @PostMapping("/courses/{courseId}/grade/{studentId}")
    public String gradeCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId, @RequestParam float grade) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()){

            return "redirect:/login";
        }
        User user = searchUser.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();

        Optional <User> searchStudent = userService.findById(studentId);
        if(searchStudent.isEmpty()){

            model.addAttribute("message", "Estudiante no encontrado");
            return "error";
        }
        User student = searchStudent.get();

        if(user.getRole() == 1 && course.getTeacher() == user){

            if(courseService.userIsInCourse(student, course)){

                if(submissionService.userMadeSubmission(student, course)){

                    submissionService.gradeSubmission(course, student, grade);
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
    }

    @GetMapping("/courses/{courseId}/submission/download/{studentId}")
    public ResponseEntity<Object> downloadCourseSubmission(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId) {

        if(userId.isEmpty()){
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()){

            return ResponseEntity.status(401).body("Unauthorized");
        }
        User user = searchUser.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()){

            return ResponseEntity.status(404).body("Course not found");
        }
        Course course = searchCourse.get();

        Optional <User> searchStudent = userService.findById(studentId);
        if(searchStudent.isEmpty()){

            return ResponseEntity.status(404).body("Student not found");
        }
        User student = searchStudent.get();
        if(user.getRole() == 1 && course.getTeacher() == user){

            if(courseService.userIsInCourse(student, course)) {

                if(submissionService.userMadeSubmission(student, course)) {

                    return submissionService.getSubmission(course, student);
                }else {

                    return ResponseEntity.status(404).body("Submission not found");
                }
            }else{

                return ResponseEntity.status(404).body("User is not inm course");
            }
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @PostMapping("/courses/{courseId}/submission")
    public String submitCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, MultipartFile submission) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()){

            return "redirect:/login";
        }
        User user = searchUser.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();

        if(submission.isEmpty()){

            model.addAttribute("message", "No se ha seleccionado un archivo");
            return "error";
        }
        if(courseService.userIsInCourse(user, course)) {

            if(!submissionService.userMadeSubmission(user, course)) {

                submissionService.save(course, user, submission);
            }
        }
        return "redirect:/courses";
    }

    @PostMapping("/courses/{courseId}/draft/{studentId}")
    public String deleteSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        Optional <User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()){

            return "redirect:/login";
        }
        User user = searchUser.get();

        Optional <Course> searchCourse = courseService.findById(courseId);
        if(searchCourse.isEmpty()){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        Course course = searchCourse.get();

        Optional <User> searchStudent = userService.findById(studentId);
        if(searchStudent.isEmpty()){

            model.addAttribute("message", "Estudiante no encontrado");
            return "error";
        }
        User student = searchStudent.get();

        if(courseService.userIsInCourse(student, course)) {

            if(submissionService.userMadeSubmission(student, course)) {

                if(course.getTeacher() == user) {

                    submissionService.deleteSubmission(student, course);
                    return "redirect:/courses/" + courseId + "/grade";
                }
            }
        }
        return "redirect:/courses";
    }
}
