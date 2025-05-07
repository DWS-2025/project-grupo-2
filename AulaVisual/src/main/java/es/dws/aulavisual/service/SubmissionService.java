package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.SubmissionDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.SubmissionMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.Submission;
import es.dws.aulavisual.model.SubmissionComment;
import es.dws.aulavisual.repository.SubmissionRepository;
import es.dws.aulavisual.model.User;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
            Submission submissionEntity = new Submission(course, user, submission.getOriginalFilename());
            saveSubmissionInDisk(submissionEntity, submission);
            submissionRepository.save(submissionEntity);
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
        return submissionMapper.toDTO(submissionRepository.save(new Submission(course, user, submissionDTO.submissionName())));
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

            Path submissionPath = java.nio.file.Paths.get("files/submissions/");
            Path path = submissionPath.resolve("course-" + courseId);
            if (!Files.exists(path)) {
                return false;
            }
            Path filePath = path.resolve(userId + ".pdf");
            if (!Files.exists(filePath)) {

                return false;
            }
            return true;
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

    public void uploadSubmissionContent(long id, String location, MultipartFile file) {

        User loggedUser = userService.getLoggedUser();
        Submission submission = submissionRepository.findById(id).orElseThrow();
        if(!loggedUser.equals(submission.getUser())){

            throw new RuntimeException("Solo puedo subir contenido a tus entregas");
        }
        if(userService.hasRoleOrHigher("TEACHER")){

            throw new RuntimeException("Solo los usuarios pueden subir contenido a sus entregas");
        }
        if(userMadeSubmission(loggedUser.getId(), submission.getId())){

            throw new RuntimeException("Ya has subido contenido a esta entrega");
        }
        submission.setContent(location);
        saveSubmissionInDisk(submission, file);
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

            try {
                Path submissionPath = java.nio.file.Paths.get("files/submissions/");
                Path path = submissionPath.resolve("course-" + submission.getCourse().getId());
                Path filePath = path.resolve(submission.getStudent().getId() + ".pdf");
                Files.deleteIfExists(filePath);
            }catch (IOException e){

                throw new RuntimeException("Error deleting file", e);
            }
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

    public List <SubmissionComment> saveComment(long courseId, String comment, long submissionId) {

        User loggedUser = userService.getLoggedUser();
        Course course = courseService.findById(courseId);
        if(courseService.userIsInCourse(loggedUser.getId(), courseId)) {

            Submission submission = submissionRepository.findById(submissionId).orElseThrow();
            // Sanitize the comment before adding it
            PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
            String sanitizedComment = policy.sanitize(comment);
            SubmissionComment submissionComment = new SubmissionComment(loggedUser.getRole(), sanitizedComment);
            submission.addComment(submissionComment);
            submissionRepository.save(submission);
            return submission.getComments();
        }
        throw new RuntimeException("No tienes permisos para comentar esta entrega");
    }

    private void saveSubmissionInDisk(Submission submission, MultipartFile file) {

        try {
            Path submissionPath = java.nio.file.Paths.get("files/submissions/");
            Path path = submissionPath.resolve("course-" + submission.getCourse().getId());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(submission.getStudent().getId() + ".pdf");
            Files.copy(file.getInputStream(), filePath);
        }catch (IOException e) {
            throw new RuntimeException("Error creating directory", e);
        }
    }

    private ResponseEntity <Object> getSubmissionContent(Submission submission) {

        try {
            Path submissionPath = java.nio.file.Paths.get("files/submissions/");
            Path path = submissionPath.resolve("course-" + submission.getCourse().getId());
            Path filePath = path.resolve(submission.getStudent().getId() + ".pdf");
            Resource file = new UrlResource(filePath.toUri());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/pdf").body(file);

        }catch (Exception e) {
            System.out.println("Error loading submission: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}