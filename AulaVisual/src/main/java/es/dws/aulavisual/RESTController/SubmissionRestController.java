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
    public ResponseEntity<List<SubmissionDTO>> getSubmissions(@PathVariable long courseId) {

        List<SubmissionDTO> submissionDTOS = submissionService.getCourseSubmissions(courseId);

        return ResponseEntity.ok(submissionDTOS);
    }

    @GetMapping("user/{userId}/submissions")
    public ResponseEntity<List<SubmissionDTO>> getUserSubmissions(@PathVariable long userId) {

        List<SubmissionDTO> submissionDTOS = submissionService.getUserSubmissions(userId);

        return ResponseEntity.ok(submissionDTOS);
    }

    @GetMapping("submission/{id}")
    public ResponseEntity<SubmissionDTO> getSubmission(@PathVariable long id) {

        SubmissionDTO submissionDTO = submissionService.findById(id);

        return ResponseEntity.ok(submissionDTO);
    }

    @PostMapping("submission/")
    public ResponseEntity<SubmissionDTO> createSubmission(@RequestBody SubmissionDTO submissionDTO) {

        SubmissionDTO createdSubmission = submissionService.save(submissionDTO);

        return ResponseEntity.ok(createdSubmission);
    }

    @PutMapping("submission/{id}/content")
    public ResponseEntity<Object> uploadSubmissionContent(@PathVariable long id, @RequestParam("file") MultipartFile file) {

        try {
            String location = fromCurrentRequest().path("").buildAndExpand(id).toUri().getPath();
            submissionService.uploadSubmissionContent(id, location, file.getInputStream(), file.getSize());
            return ResponseEntity.created(URI.create(location)).body(location);
        }catch (IOException e){

            return ResponseEntity.badRequest().body("Error uploading submission content: " + e.getMessage());
        }
    }

    @GetMapping("submission/{id}/content")
    public ResponseEntity<Object> viewSubmission(@PathVariable long id) {

        try {
            return submissionService.getSubmission(id);
        }catch (Exception e){

            return ResponseEntity.badRequest().body("Error loading submission content: " + e.getMessage());
        }
    }

    public record gradeUpdate(
            Float grade
    ){}
    @PutMapping("submission/{id}/grade")
    public ResponseEntity<SubmissionDTO> gradeSubmission(@PathVariable long id, @RequestBody gradeUpdate grade) {

        SubmissionDTO submissionDTO = submissionService.gradeSubmission(id, grade.grade());
        return ResponseEntity.ok(submissionDTO);
    }

    @DeleteMapping("submission/{id}")
    public ResponseEntity<SubmissionDTO> deleteSubmission(@PathVariable long id) {

        SubmissionDTO submissionDTO = submissionService.deleteSubmission(id);
        return ResponseEntity.ok().body(submissionDTO);
    }
}
