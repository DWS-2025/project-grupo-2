package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/")
public class SubmissionRestController {

    private final SubmissionService submissionService;

    public SubmissionRestController(SubmissionService submissionService) {

        this.submissionService = submissionService;
    }

    @GetMapping("course/{courseId}/submissions")
    public ResponseEntity<Object> getSubmissions(@PathVariable long courseId) {

        try {
            List<SubmissionDTO> submissionDTOS = submissionService.getCourseSubmissions(courseId);
            return ResponseEntity.ok(submissionDTOS);
        }catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("user/{userId}/submissions")
    public ResponseEntity<Object> getUserSubmissions(@PathVariable long userId) {

        try {

            List<SubmissionDTO> submissionDTOS = submissionService.getUserSubmissions(userId);
            return ResponseEntity.ok(submissionDTOS);
        }catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("submission/{id}")
    public ResponseEntity<Object> getSubmission(@PathVariable long id) {

        try {
            SubmissionDTO submissionDTO = submissionService.findById(id);
            return ResponseEntity.ok(submissionDTO);
        }catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("submission/")
    public ResponseEntity<Object> createSubmission(@RequestBody SubmissionDTO submissionDTO) {

        try {
            SubmissionDTO createdSubmission = submissionService.save(submissionDTO);
            return ResponseEntity.ok(createdSubmission);
        }catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("submission/{id}/content")
    public ResponseEntity<Object> uploadSubmissionContent(@PathVariable long id, @RequestParam("file") MultipartFile file) {

        try {
            String location = fromCurrentRequest().path("").buildAndExpand(id).toUri().getPath();
            submissionService.uploadSubmissionContent(id, location, file);
            return ResponseEntity.created(URI.create(location)).body(location);
        }catch (RuntimeException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("submission/{id}/content")
    public ResponseEntity<Object> viewSubmission(@PathVariable long id) {

        try {
            return submissionService.getSubmission(id);
        }catch (Exception e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record gradeUpdate(
            Float grade
    ){}
    @PutMapping("submission/{id}/grade")
    public ResponseEntity<Object> gradeSubmission(@PathVariable long id, @RequestBody gradeUpdate grade) {

        try {
            SubmissionDTO submissionDTO = submissionService.gradeSubmission(id, grade.grade());
            return ResponseEntity.ok(submissionDTO);
        }catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("submission/{id}")
    public ResponseEntity<SubmissionDTO> deleteSubmission(@PathVariable long id) {

        SubmissionDTO submissionDTO = submissionService.deleteSubmission(id);
        return ResponseEntity.ok().body(submissionDTO);
    }

    @PostMapping("/course/{courseId}/submission/{id}/comment")
    public ResponseEntity<Object> commentSubmission(@PathVariable long courseId,@PathVariable long id, @RequestBody String comment) {

        try {

            return ResponseEntity.ok().body(submissionService.saveComment(courseId, comment, id));
        }catch (RuntimeException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}/submission/{id}/comments")
    public ResponseEntity<Object> seeSubmissionComments(@PathVariable long id) {

        try {
            SubmissionDTO submissionDTO = submissionService.findById(id);
            return ResponseEntity.ok().body(submissionDTO.comments());
        }catch (RuntimeException e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
