package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.SubmissionMapper;
import es.dws.aulavisual.Mapper.UserMapper;
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

    public SubmissionService(SubmissionRepository submissionRepository, SubmissionMapper submissionMapper, UserMapper userMapper) {
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
        this.userMapper = userMapper;
    }

    public void save(Course course, UserDTO userDTO, MultipartFile submission) {

        User user = userMapper.toDomain(userDTO);
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

    public SubmissionDTO findByUserAndCourse(UserDTO userDTO, Course course) {

        User user = userMapper.toDomain(userDTO);
        return submissionMapper.toDTO(submissionRepository.findByStudentAndCourse(user, course).orElseThrow());
    }

    public boolean userMadeSubmission(UserDTO userDTO, Course course) {

        User user = userMapper.toDomain(userDTO);
        Optional <Submission> submission = submissionRepository.findByStudentAndCourse(user, course);
        return submission.isPresent();
    }

    public List<SubmissionDTO> getSubmissions(Course course, boolean isGraded) {

        return submissionMapper.toDTOs(submissionRepository.findSubmissionByCourseAndGraded(course, isGraded));
    }

    public void gradeSubmission(Course course, UserDTO studentDTO, float grade) {

        User student = userMapper.toDomain(studentDTO);
        Optional <Submission> searchSubmission = submissionRepository.findByStudentAndCourse(student, course);
        if(searchSubmission.isPresent()) {

            Submission submission = searchSubmission.get();
            submission.setGrade(grade);
            submissionRepository.save(submission);
        }
    }

    public ResponseEntity <Object> getSubmission(Course course, UserDTO studentDTO) {

        User student = userMapper.toDomain(studentDTO);
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

    public void deleteSubmission(UserDTO studentDTO, Course course) {

        User student = userMapper.toDomain(studentDTO);
        Optional <Submission> searchSubmission = submissionRepository.findByStudentAndCourse(student, course);
        searchSubmission.ifPresent(submissionRepository::delete);
    }
}