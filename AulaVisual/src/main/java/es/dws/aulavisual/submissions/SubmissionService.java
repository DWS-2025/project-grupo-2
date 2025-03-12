package es.dws.aulavisual.submissions;

import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.users.User;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.sql.Blob;
import org.hibernate.engine.jdbc.BlobProxy;
import java.util.Optional;
import java.io.IOException;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public void save(Course course, User user, MultipartFile submission) {

        submissionRepository.save(new Submission(course, user, transformSubmission(submission)));
    }

    private Blob transformSubmission(MultipartFile submission) {
        try {
            return BlobProxy.generateProxy(submission.getInputStream(), submission.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Error processing submission", e);
        }
    }

    public Optional<Submission> findById(long id) {

        return submissionRepository.findById(id);
    }

    public Optional<Submission> findByUserAndCourse(User user, Course course) {

        return submissionRepository.findByUserAndCourse(user, course);
    }

    public boolean userMadeSubmission(User user, Course course) {

        Optional <Submission> submission = submissionRepository.findByUserAndCourse(user, course);
        return submission.isPresent();
    }

    public boolean isgraded(User user, Course course) {
        Optional <Submission> submission = submissionRepository.findByUserAndCourse(user, course);

        return submission.map(Submission::isGraded).orElse(false);

    }

    public List<Submission> getSubmissions(Course course, boolean isGraded) {

        return submissionRepository.findSubmissionByCourseAndGraded(course, isGraded);
    }

    public void gradeSubmission(Course course, User student, float grade) {

        Optional <Submission> searchSubmission = submissionRepository.findByUserAndCourse(student, course);
        if(searchSubmission.isPresent()) {

            Submission submission = searchSubmission.get();
            submission.setGrade(grade);
            submissionRepository.save(submission);
        }
    }

    public ResponseEntity <Object> getSubmission(Course course, User student) {

        Optional <Submission> searchSubmission = submissionRepository.findByUserAndCourse(student, course);
        if(searchSubmission.isPresent()) {

            Submission submission = searchSubmission.get();
            try {
                return ResponseEntity.ok().header("Content-Type", "application/pdf")
                        .contentLength(submission.getSubmission().length()).body(submission.getSubmission());

            }catch (Exception e) {
                System.out.println("Error loading submission: " + e.getMessage());
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    public void deleteSubmission(User student, Course course) {

        Optional <Submission> searchSubmission = submissionRepository.findByUserAndCourse(student, course);
        searchSubmission.ifPresent(submissionRepository::delete);
    }
}