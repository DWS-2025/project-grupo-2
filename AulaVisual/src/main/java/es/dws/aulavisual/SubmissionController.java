package es.dws.aulavisual;

import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.CourseManager;
import es.dws.aulavisual.submissions.Submission;
import es.dws.aulavisual.submissions.SubmissionManager;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.users.UserService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import es.dws.aulavisual.users.User;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class SubmissionController {

    private final UserService userService;
    private final SubmissionManager submissionManager;
    private final CourseManager courseManager;

    public SubmissionController(UserService userService, SubmissionManager submissionManager, CourseManager courseManager) {
        this.userService = userService;
        this.submissionManager = submissionManager;
        this.courseManager = courseManager;
    }

    @GetMapping("/courses/{courseId}/submission")
    public String seeCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        User user = userService.getUser(Long.parseLong(userId));
        if(user == null){

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        Course course = courseManager.getCourse(courseId);
        if(course == null){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        if(user.getRole() == 1){

            return "redirect:/courses/" + courseId + "/grade";
        }

        if(user.getRole() == 2){

            if(courseManager.userInCourse(courseId, Long.parseLong(userId))){

                model.addAttribute("courseId", courseId);
                model.addAttribute("courseName", courseManager.getCourse(courseId).getName());
                if(!courseManager.userMadeSubmission(courseId, Long.parseLong(userId))){

                    model.addAttribute("submitted", false);
                    model.addAttribute("task", courseManager.getTask(courseId));
                }else {

                    model.addAttribute("submitted", true);
                    if(courseManager.isgraded(courseId, Long.parseLong(userId))) {

                        model.addAttribute("grade", courseManager.getGrade(courseId, Long.parseLong(userId)));
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
        User user = userService.getUser(Long.parseLong(userId));
        if(user == null){

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        Course course = courseManager.getCourse(courseId);
        if(course == null){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        if(user.getRole() == 1){

            List<Submission> submissions = course.getUngradedSubmission();
            List<Submission> graded = course.getGradedSubmissions();
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
        User user = userService.getUser(Long.parseLong(userId));
        if(user == null){

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        Course course = courseManager.getCourse(courseId);
        if(course == null){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        User student = userService.getUser(studentId);
        if(student == null){

            model.addAttribute("message", "Estudiante no encontrado");
            return "error";
        }
        if(user.getRole() == 1 && courseManager.getTeacherId(courseId) == Long.parseLong(userId)){

            courseManager.gradeSubmission(courseId, studentId, grade);
            return "redirect:/courses/" + courseId + "/grade";
        }
        return "redirect:/courses";
    }

    @GetMapping("/courses/{courseId}/submission/download/{studentId}")
    public ResponseEntity<Object> downloadCourseSubmission(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId) {

        if(userId.isEmpty()){
            return ResponseEntity.status(401).body("Unauthorized");
        }
        User user = userService.getUser(Long.parseLong(userId));
        if(user == null){

            return ResponseEntity.status(401).body("Unauthorized");
        }
        if(user.getRole() == 1 && courseManager.getTeacherId(courseId) == Long.parseLong(userId)){

            return submissionManager.getSubmission(studentId, courseId);
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    @PostMapping("/courses/{courseId}/submission")
    public String submitCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, MultipartFile submission) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        User user = userService.getUser(Long.parseLong(userId));
        if(user == null){

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        Course course = courseManager.getCourse(courseId);
        if(course == null){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        if(submission.isEmpty()){

            model.addAttribute("message", "No se ha seleccionado un archivo");
            return "redirect:/courses/" + courseId + "/submission";
        }
        if(courseManager.userInCourse(courseId, Long.parseLong(userId))) {

            if(!courseManager.userMadeSubmission(courseId, Long.parseLong(userId))) {

                if(submissionManager.submitCourseSubmission(Long.parseLong(userId), courseId, submission)) {
                    course.saveSubmission(userService.getUser(Long.parseLong(userId)));
                    return "redirect:/courses";
                }else {

                    return "redirect:/courses/" + courseId + "/submission";
                }
            }
        }
        return "redirect:/courses";
    }

    @PostMapping("/courses/{courseId}/draft/{studentId}")
    public String deleteSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        User user = userService.getUser(Long.parseLong(userId));
        if(user == null){

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        Course course = courseManager.getCourse(courseId);
        if(course == null){

            model.addAttribute("message", "Curso no encontrado");
            return "error";
        }
        if(courseManager.userInCourse(courseId, Long.parseLong(userId))) {

            if(courseManager.userMadeSubmission(courseId, studentId)) {

                if(courseManager.getTeacherId(courseId) == Long.parseLong(userId)) {

                    courseManager.deleteSubmission(courseId, studentId);
                    submissionManager.deleteSubmission(studentId, courseId);
                    return "redirect:/courses/" + courseId + "/grade";
                }
            }
        }
        return "redirect:/courses";
    }
}
