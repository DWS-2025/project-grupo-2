package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("submission/{id}")
    public ResponseEntity<SubmissionDTO> getSubmission(@PathVariable long id) {

        SubmissionDTO submissionDTO = submissionService.findById(id);

        return ResponseEntity.ok(submissionDTO);
    }
}
