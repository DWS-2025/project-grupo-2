package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.SubmissionMapper;
import es.dws.aulavisual.Mapper.UserMapper;
import es.dws.aulavisual.Mapper.CourseMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Submission;
import es.dws.aulavisual.repository.SubmissionRepository;
import es.dws.aulavisual.model.User;
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
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    public SubmissionService(SubmissionRepository submissionRepository, SubmissionMapper submissionMapper, UserMapper userMapper, CourseMapper courseMapper) {
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
    }

    public void save(CourseDTO courseDTO, UserDTO userDTO, MultipartFile submission) {

        User user = userMapper.toDomain(userDTO);
        Course course = courseMapper.toDomain(courseDTO);
        submissionRepository.save(new Submission(course, user, transformSubmission(submission)));
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

        User user = userMapper.toDomain(userDTO);
        Course course = courseMapper.toDomain(courseDTO);
        return submissionMapper.toDTO(submissionRepository.findByStudentAndCourse(user, course).orElseThrow());
    }

    public boolean userMadeSubmission(UserDTO userDTO, CourseDTO courseDTO) {

        User user = userMapper.toDomain(userDTO);
        Course course = courseMapper.toDomain(courseDTO);
        Optional <Submission> submission = submissionRepository.findByStudentAndCourse(user, course);
        return submission.isPresent();
    }

    public List<SubmissionDTO> getSubmissions(CourseDTO courseDTO, boolean isGraded) {

        Course course = courseMapper.toDomain(courseDTO);
        return submissionMapper.toDTOs(submissionRepository.findSubmissionByCourseAndGraded(course, isGraded));
    }

    public void gradeSubmission(CourseDTO courseDTO, UserDTO studentDTO, float grade) {

        Course course = courseMapper.toDomain(courseDTO);
        User student = userMapper.toDomain(studentDTO);
        Optional <Submission> searchSubmission = submissionRepository.findByStudentAndCourse(student, course);
        if(searchSubmission.isPresent()) {

            Submission submission = searchSubmission.get();
            submission.setGrade(grade);
            submissionRepository.save(submission);
        }
    }

    public ResponseEntity <Object> getSubmission(CourseDTO courseDTO, UserDTO studentDTO) {

        User student = userMapper.toDomain(studentDTO);
        Course course = courseMapper.toDomain(courseDTO);
        Optional <Submission> searchSubmission = submissionRepository.findByStudentAndCourse(student, course);
        if(searchSubmission.isPresent()) {

            Submission submission = searchSubmission.get();
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
        return ResponseEntity.notFound().build();
    }

    public void deleteSubmission(UserDTO studentDTO, CourseDTO courseDTO) {

        User student = userMapper.toDomain(studentDTO);
        Course course = courseMapper.toDomain(courseDTO);
        Optional <Submission> searchSubmission = submissionRepository.findByStudentAndCourse(student, course);
        searchSubmission.ifPresent(submissionRepository::delete);
    }
}