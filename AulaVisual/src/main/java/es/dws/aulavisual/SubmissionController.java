package es.dws.aulavisual;

import es.dws.aulavisual.courses.CourseManager;
import es.dws.aulavisual.submissions.SubmissionManager;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.users.UserManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import es.dws.aulavisual.users.User;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

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

            return "redirect:/courses";
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
                }
            }

            return "courses-user/submission";
        }
        return "redirect:/courses";
    }

    @GetMapping("/courses/{courseId}/submission/download")
    public ResponseEntity<Object> downloadCourseSubmission(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long courseId) {

        if(userId.isEmpty()){
            return null;
        }
        return submissionManager.getSubmission(Long.parseLong(userId), courseId);
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
                    //Add submission to course
                    return "redirect:/courses";
                }else {

                    return "redirect:/courses/" + courseId + "/submission";
                }
            }
        }
        return "redirect:/courses";
    }
}
