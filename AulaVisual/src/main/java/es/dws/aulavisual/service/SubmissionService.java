package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.SubmissionMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Submission;
import es.dws.aulavisual.repository.SubmissionRepository;
import es.dws.aulavisual.model.User;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
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

        User student = userService.getLoggedUser();
        if(student.getId() == userDTO.id() && courseService.userIsInCourse(student.getId(), courseDTO.id()) && !userMadeSubmission(student.getId(), courseDTO.id())) {

            User user = userService.findById(userDTO.id());
            Course course = courseService.findById(courseDTO.id());
            submissionRepository.save(new Submission(course, user, transformSubmission(submission)));
        }
    }

    public SubmissionDTO save(SubmissionDTO submissionDTO) {

        if(submissionDTO.student() == null || submissionDTO.course() == null) {
            throw new RuntimeException("Submission must have a student and a course");
        }
        Course course = courseService.findById(submissionDTO.course().id());
        User user = userService.findById(submissionDTO.student().id());
        User loggedUser = userService.getLoggedUser();
        if(userService.hasRoleOrHigher("TEACHER")){

            throw new RuntimeException("Solo los usuarios pueden crear una submission");
        }
        if(!loggedUser.equals(user)){

            throw new RuntimeException("Crea un submission para ti mismo melón");
        }
        if(!courseService.userIsInCourse(loggedUser.getId(), course.getId())){

            throw new RuntimeException("Debes estar matriculado en el curso");
        }
        if(userMadeSubmission(user.getId(), course.getId())){

            throw new RuntimeException("Ya has creado una submission para este curso");
        }
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

        User loggedUser = userService.getLoggedUser();
        Submission submission = submissionRepository.findById(id).orElseThrow();
        if(submission.getUser().equals(loggedUser) || (userService.hasRoleOrHigher("TEACHER") && loggedUser.getCourseTeaching().equals(submission.getCourse()))) {

            return submissionMapper.toDTO(submissionRepository.findById(id).orElseThrow());
        }

        throw new RuntimeException("User does not have sufficient privileges");
    }

    public SubmissionDTO findByUserAndCourse(UserDTO userDTO, CourseDTO courseDTO) {

        if(userService.hasRoleOrHigher("TEACHER") || courseService.userIsInCourse(userDTO.id(), courseDTO.id())) {

            User user = userService.findById(userDTO.id());
            Course course = courseService.findById(courseDTO.id());
            return submissionMapper.toDTO(submissionRepository.findByStudentAndCourse(user, course).orElseThrow());
        }
        throw new RuntimeException("User does not have sufficient privileges");
    }

    public boolean userMadeSubmission(Long userId, Long courseId) {

        if(userService.hasRoleOrHigher("TEACHER") || courseService.userIsInCourse(userId, courseId)) {

            User user = userService.findById(userId);
            Course course = courseService.findById(courseId);
            Optional <Submission> submission = submissionRepository.findByStudentAndCourse(user, course);
            return submission.isPresent();
        }
        throw new RuntimeException("User does not have sufficient privileges");
    }

    public List<SubmissionDTO> getSubmissions(CourseDTO courseDTO, boolean isGraded) {

        if(userService.hasRoleOrHigher("TEACHER")) {

            Course course = courseService.findById(courseDTO.id());
            return submissionMapper.toDTOs(submissionRepository.findSubmissionByCourseAndGraded(course, isGraded));
        }
        throw new RuntimeException("User does not have sufficient privileges");
    }

    public void gradeSubmission(CourseDTO courseDTO, UserDTO studentDTO, float grade) {

        Course course = courseService.findById(courseDTO.id());
        User student = userService.findById(studentDTO.id());
        Submission searchSubmission = submissionRepository.findByStudentAndCourse(student, course).orElseThrow();
        gradeSubmission(searchSubmission.getId(), grade);
    }

    public ResponseEntity <Object> getSubmission(CourseDTO courseDTO, UserDTO studentDTO) {

        Course course = courseService.findById(courseDTO.id());
        User user = userService.findById(studentDTO.id());
        Submission submission = submissionRepository.findByStudentAndCourse(user, course).orElseThrow();
        return getSubmission(submission.getId());
    }

    public void deleteSubmission(UserDTO studentDTO, CourseDTO courseDTO) {

        User user = userService.findById(studentDTO.id());
        Course course = courseService.findById(courseDTO.id());
        Submission submission = submissionRepository.findByStudentAndCourse(user, course).orElseThrow();
        deleteSubmission(submission.getId());
    }

    public List<SubmissionDTO> getCourseSubmissions(long courseId) {

        if(userService.hasRoleOrHigher("TEACHER")) {

            User loggedUser = userService.getLoggedUser();
            if(userService.hasRoleOrHigher("ADMIN") ||(loggedUser.getId() == courseService.findById(courseId).getTeacher().getId())) {

                Course course = courseService.findById(courseId);
                return submissionMapper.toDTOs(submissionRepository.findSubmissionByCourse(course));
            }
        }
        throw new RuntimeException("User does not have sufficient privileges");
    }

    public List<SubmissionDTO> getUserSubmissions(long userId) {

        User loggedUser = userService.getLoggedUser();
        if(userService.hasRoleOrHigher("ADMIN") || loggedUser.getId() == userId) {

            User user = userService.findById(userId);
            return submissionMapper.toDTOs(submissionRepository.findSubmissionByStudent(user));
        }
        throw new RuntimeException("User does not have sufficient privileges");
    }

    public void uploadSubmissionContent(long id, String location, InputStream inputStream, long size) {

        User loggedUser = userService.getLoggedUser();
        Submission submission = submissionRepository.findById(id).orElseThrow();
        if(!loggedUser.equals(submission.getUser())){

            throw new RuntimeException("Solo puedo subir contenido a tus entregas");
        }
        if(userService.hasRoleOrHigher("TEACHER")){

            throw new RuntimeException("Solo los usuarios pueden subir contenido a sus entregas");
        }
        if(submission.getSubmission() != null){

            throw new RuntimeException("Ya has subido contenido a esta entrega");
        }
        submission.setContent(location);
        submission.setSubmission(BlobProxy.generateProxy(inputStream, size));
        submissionRepository.save(submission);
    }

    public ResponseEntity <Object> getSubmission(long id) {

        User loggedUser = userService.getLoggedUser();
        Submission submission = submissionRepository.findById(id).orElseThrow();
        if(!userService.hasRoleOrHigher("TEACHER") && !loggedUser.equals(submission.getUser())){

            throw new RuntimeException("No tienes permisos para ver esta entrega");
        }
        if(loggedUser.getRole().equals("TEACHER") && !submission.getCourse().getTeacher().equals(loggedUser)){

            throw new RuntimeException("Debes ser el profesor de este curso");
        }
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

        User loggedUser = userService.getLoggedUser();
        Submission submission = submissionRepository.findById(id).orElseThrow();
        if(loggedUser.getRole().equals("ADMIN") || loggedUser.getId() == submission.getCourse().getTeacher().getId()) {

            if(submission.isGraded()){

                throw new RuntimeException("Entrega ya calificada");
            }
            return grade(submission, grade);
        }
        throw new RuntimeException("No tienes permisos para calificar esta entrega");
    }

    private SubmissionDTO grade(Submission submission, float grade) {

        submission.setGrade(grade);
        return submissionMapper.toDTO(submissionRepository.save(submission));
    }

    public SubmissionDTO deleteSubmission(long id) {

        User loggedUser = userService.getLoggedUser();
        Submission submission = submissionRepository.findById(id).orElseThrow();
        if(loggedUser.getRole().equals("ADMIN") || loggedUser.getId() == submission.getCourse().getTeacher().getId()) {

            return delete(submission);
        }
        throw new RuntimeException("No tienes permisos para eliminar esta entrega");
    }

    private SubmissionDTO delete(Submission submission) {

        submission.getStudent().getSubmissions().remove(submission);
        submission.getCourse().getSubmissions().remove(submission);
        submissionRepository.delete(submission);
        return submissionMapper.toDTO(submission);
    }

    public List <String> saveComment(long courseId, String comment) {

        User loggedUser = userService.getLoggedUser();
        Course course = courseService.findById(courseId);
        if(courseService.userIsInCourse(loggedUser.getId(), courseId)) {

            Submission submission = submissionRepository.findByStudentAndCourse(loggedUser, course).orElseThrow();
            // Sanitize the comment before adding it
            PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
            String sanitizedComment = policy.sanitize(comment);
            submission.addComment(sanitizedComment);
            submissionRepository.save(submission);
            return submission.getComments();
        }
        throw new RuntimeException("No tienes permisos para comentar esta entrega");
    }
}