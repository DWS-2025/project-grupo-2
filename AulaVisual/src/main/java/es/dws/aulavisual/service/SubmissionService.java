package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.SubmissionMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Submission;
import es.dws.aulavisual.repository.SubmissionRepository;
import es.dws.aulavisual.model.User;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    private final SubmissionMapper submissionMapper;
    private final CourseService courseService;
    private final UserService userService;

    public SubmissionService(SubmissionRepository submissionRepository, SubmissionMapper submissionMapper, CourseService courseService, UserService userService) {
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
        this.courseService = courseService;
        this.userService = userService;
    }

    public void save(CourseDTO courseDTO, UserDTO userDTO, MultipartFile submission) {

        User user = userService.findById(userDTO.id());
        Course course = courseService.findById(courseDTO.id());
        submissionRepository.save(new Submission(course, user, transformSubmission(submission)));
    }

    public SubmissionDTO save(SubmissionDTO submissionDTO) {

        Course course = courseService.findById(submissionDTO.course().id());
        User user = userService.findById(submissionDTO.student().id());
        return submissionMapper.toDTO(submissionRepository.save(new Submission(course, user, null)));
    }

    private Blob transformSubmission(MultipartFile submission) {
        try {
            return BlobProxy.generateProxy(submission.getInputStream(), submission.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Error processing submission", e);
        }
    }

    public SubmissionDTO findById(long id) {

        return submissionMapper.toDTO(submissionRepository.findById(id).orElseThrow());
    }

    public SubmissionDTO findByUserAndCourse(UserDTO userDTO, CourseDTO courseDTO) {

        User user = userService.findById(userDTO.id());
        Course course = courseService.findById(courseDTO.id());
        return submissionMapper.toDTO(submissionRepository.findByStudentAndCourse(user, course).orElseThrow());
    }

    public boolean userMadeSubmission(UserDTO userDTO, CourseDTO courseDTO) {

        User user = userService.findById(userDTO.id());
        Course course = courseService.findById(courseDTO.id());
        Optional <Submission> submission = submissionRepository.findByStudentAndCourse(user, course);
        return submission.isPresent();
    }

    public List<SubmissionDTO> getSubmissions(CourseDTO courseDTO, boolean isGraded) {

        Course course = courseService.findById(courseDTO.id());
        return submissionMapper.toDTOs(submissionRepository.findSubmissionByCourseAndGraded(course, isGraded));
    }

    public void gradeSubmission(CourseDTO courseDTO, UserDTO studentDTO, float grade) {

        Course course = courseService.findById(courseDTO.id());
        User student = userService.findById(studentDTO.id());
        Optional <Submission> searchSubmission = submissionRepository.findByStudentAndCourse(student, course);
        searchSubmission.ifPresent(submission -> grade(submission, grade));
    }

    public ResponseEntity <Object> getSubmission(CourseDTO courseDTO, UserDTO studentDTO) {

        User student = userService.findById(studentDTO.id());
        Course course = courseService.findById(courseDTO.id());
        Optional <Submission> searchSubmission = submissionRepository.findByStudentAndCourse(student, course);
        if(searchSubmission.isPresent()) {

            Submission submission = searchSubmission.get();
            return getSubmissionContent(submission);
        }
        return ResponseEntity.notFound().build();
    }

    public void deleteSubmission(UserDTO studentDTO, CourseDTO courseDTO) {

        User student = userService.findById(studentDTO.id());
        Course course = courseService.findById(courseDTO.id());
        Submission submission = submissionRepository.findByStudentAndCourse(student, course).orElseThrow();
        delete(submission);
    }

    public List<SubmissionDTO> getCourseSubmissions(long courseId) {

        Course course = courseService.findById(courseId);
        return submissionMapper.toDTOs(submissionRepository.findSubmissionByCourse(course));
    }

    public List<SubmissionDTO> getUserSubmissions(long userId) {

        User user = userService.findById(userId);
        return submissionMapper.toDTOs(submissionRepository.findSubmissionByStudent(user));
    }

    public void uploadSubmissionContent(long id, String location, InputStream inputStream, long size) {

        Submission submission = submissionRepository.findById(id).orElseThrow();

        submission.setContent(location);
        submission.setSubmission(BlobProxy.generateProxy(inputStream, size));
        submissionRepository.save(submission);
    }

    public ResponseEntity <Object> getSubmission(long id) {

        Submission submission = submissionRepository.findById(id).orElseThrow();
        return getSubmissionContent(submission);
    }

    private ResponseEntity <Object> getSubmissionContent(Submission submission) {

        Blob content = submission.getSubmission();
        try {
            Resource file = new InputStreamResource(content.getBinaryStream());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .contentLength(content.length()).body(file);

        }catch (Exception e) {
            System.out.println("Error loading submission: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    public SubmissionDTO gradeSubmission(long id, float grade) {

        Submission submission = submissionRepository.findById(id).orElseThrow();
        return grade(submission, grade);
    }

    private SubmissionDTO grade(Submission submission, float grade) {

        submission.setGrade(grade);
        return submissionMapper.toDTO(submissionRepository.save(submission));
    }

    public SubmissionDTO deleteSubmission(long id) {

        Submission submission = submissionRepository.findById(id).orElseThrow();
        return delete(submission);
    }

    private SubmissionDTO delete(Submission submission) {

        submission.getStudent().getSubmissions().remove(submission);
        submission.getCourse().getSubmissions().remove(submission);
        submissionRepository.delete(submission);
        return submissionMapper.toDTO(submission);
    }

    public boolean hasSubmission(long userId, long courseId) {
        User user = userService.findById(userId);
        Course course = courseService.findById(courseId);
        return submissionRepository.findByStudentAndCourse(user, course).isPresent();
    }
}