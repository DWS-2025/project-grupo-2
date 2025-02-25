package es.dws.aulavisual;

import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.courses.CourseManager;
import es.dws.aulavisual.submissions.Submission;
import es.dws.aulavisual.submissions.SubmissionManager;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.users.UserManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import es.dws.aulavisual.users.User;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SubmissionController {

    private final UserManager userManager;
    private final SubmissionManager submissionManager;
    private final CourseManager courseManager;

    public SubmissionController(UserManager userManager, SubmissionManager submissionManager, CourseManager courseManager) {
        this.userManager = userManager;
        this.submissionManager = submissionManager;
        this.courseManager = courseManager;
    }

    @GetMapping("/courses/{courseId}/submission")
    public String seeCourseSubmission(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));

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
        User user = userManager.getUser(Long.parseLong(userId));
        Course course = courseManager.getCourse(courseId);
        if(user.getRole() == 1){

            List<Submission> submissions = course.getUngradedSubmission();
            model.addAttribute("submissions", submissions);
            model.addAttribute("courseId", courseId);
            model.addAttribute("courseName", course.getName());
            return "courses-user/submissionsGrade";
        }
        return "redirect:/courses";
    }

    @PostMapping("/courses/{courseId}/grade/{studentId}")
    public String gradeCourseSubmission(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId, @RequestParam float grade) {

        if(userId.isEmpty()){
            return "redirect:/login";
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() == 1 && courseManager.getTeacherId(courseId) == Long.parseLong(userId)){

            courseManager.gradeSubmission(courseId, studentId, grade);
            return "redirect:/courses/" + courseId + "/grade";
        }
        return "redirect:/courses";
    }

    @GetMapping("/courses/{courseId}/submission/download/{studentId}")
    public ResponseEntity<Object> downloadCourseSubmission(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, @PathVariable long studentId) {

        if(userId.isEmpty()){
            return null;
        }
        User user = userManager.getUser(Long.parseLong(userId));
        if(user.getRole() == 1 && courseManager.getTeacherId(courseId) == Long.parseLong(userId)){

            return submissionManager.getSubmission(studentId, courseId);
        }
        return null;
    }

    @PostMapping("/courses/{courseId}/submission")
    public String submitCourseSubmission(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId, MultipartFile submission) {

        if(submission.isEmpty()){
            return "redirect:/courses/" + courseId + "/submission";
        }
        if(userId.isEmpty()){
            return "redirect:/login";
        }

        if(courseManager.userInCourse(courseId, Long.parseLong(userId))) {

            if(!courseManager.userMadeSubmission(courseId, Long.parseLong(userId))) {

                if(submissionManager.submitCourseSubmission(Long.parseLong(userId), courseId, submission)) {
                    Course course = courseManager.getCourse(courseId);
                    course.saveSubmission(userManager.getUser(Long.parseLong(userId)));
                    return "redirect:/courses";
                }else {

                    return "redirect:/courses/" + courseId + "/submission";
                }
            }
        }
        return "redirect:/courses";
    }
}
