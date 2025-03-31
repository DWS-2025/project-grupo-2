package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.CourseInfoDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.DTO.UserSimpleDTO;
import es.dws.aulavisual.Mapper.CourseMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.repository.CourseRepository;
import es.dws.aulavisual.model.User;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;

import org.springframework.http.ResponseEntity;

import java.util.List;

import es.dws.aulavisual.Mapper.UserMapper;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;


    public CourseService(CourseRepository courseRepository, UserService userService, UserMapper userMapper, CourseMapper courseMapper) {

        this.courseRepository = courseRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
    }

    public void assignTeacher(UserDTO teacherDTO, CourseDTO courseDTO) {

        Course course = courseRepository.findById(courseDTO.id()).orElseThrow();
        User teacher = userService.findById(teacherDTO.id());
        course.setTeacher(teacher);
        courseRepository.save(course);
        userService.addCourseToTeacher(teacherDTO, course);
    }

    public CourseDTO saveDTO(CourseDTO courseDTO) {

        Course course = courseMapper.toDomain(courseDTO);
        return courseMapper.toDTO(save(course));
    }

    public Course save(Course course) {

        return courseRepository.save(course);
    }

    public void addUserToCourse(CourseDTO courseDTO, UserDTO userDTO) {

        // Course course = courseMapper.toDomain(courseDTO);
        Course course = findById(courseDTO.id());
        //User user = userMapper.toDomain(userDTO);
        User user = userService.findById(userDTO.id());
        course.getStudents().add(user);
        //user.getCourses().add(course);
        //userService.save(user);
        courseRepository.save(course);
    }

    void removeUserFromCourse(Course course, User user) {

        course.getStudents().remove(user);
    }

    public List<CourseDTO> getCourses() {

        return courseMapper.toDTOs(courseRepository.findAll());
    }

    public CourseDTO findByIdDTO(long id) {

        return courseMapper.toDTO(findById(id));
    }

    Course findById(long id) {

        return courseRepository.findById(id).orElseThrow();
    }

    public boolean userIsInCourse(UserDTO userDTO, CourseDTO courseDTO) {

        User user = userService.findById(userDTO.id());
        Course course = courseRepository.findById(courseDTO.id()).orElseThrow();
        return course.getTeacher().equals(user)|| course.getStudents().contains(user);
    }

    public void deleteCourse(long courseId) {

        Course course = courseRepository.findById(courseId).orElseThrow();
//      userService.removeAllUsersFromCourse(course);
        removeCourseFromTeacher(course.getTeacher(), course);
        courseRepository.deleteById(courseId);
        System.out.println("Hola");
    }

    void removeCourseFromTeacher(User teacher, Course course) {

        teacher.setCourseTeaching(null);
    }

    public ResponseEntity<Object> loadImage(Long id){

        try {

            Course course = courseRepository.findById(id).orElseThrow();
            Blob image = course.getImageCourse();
            if(image == null) {

                try {
                    ClassPathResource resource = new ClassPathResource("static/images/course-default.png");
                    byte [] imageBytes = resource.getInputStream().readAllBytes();
                    return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png").body(imageBytes);
                } catch (IOException e) {

                    return ResponseEntity.status(500).body("Internal Server Error");
                }
            }else{

                Resource file = new InputStreamResource(image.getBinaryStream());
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png")
                        .contentLength(image.length()).body(file);
            }
        } catch (Exception e) {

            System.out.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    public List<CourseDTO> courseOfUser(Long id) {

        User user = userService.findById(id);
        UserDTO userDTO = userMapper.toDTO(user);
        List<Course> normalCourses = courseRepository.searchCoursesByStudentsContaining(user);
        normalCourses.addAll(courseOfTeacher(userDTO));
        return courseMapper.toDTOs(normalCourses);
    }

    public List<CourseDTO> notCourseOfUser(UserDTO userDTO) {

        User user = userService.findById(userDTO.id());
        List<Course> notCourses = courseRepository.searchCoursesByStudentsNotContaining(user);
        notCourses.removeAll(courseOfTeacher(userDTO));
        return courseMapper.toDTOs(notCourses);
    }

    List<Course> courseOfTeacher(UserDTO userDTO) {

        User user = userService.findById(userDTO.id());
        return courseRepository.searchCoursesByTeacherId(user.getId());
    }

    public List<CourseInfoDTO> courseInfoOfUser(Long id) {

        User user = userService.findById(id);
        List<Course> normalCourses = courseRepository.searchCoursesByStudentsContaining(user);
        return courseMapper.toInfoDTOs(normalCourses);
    }

    public List<UserSimpleDTO> getAllStudentsfromCourse(Long id) {

        Course course = courseRepository.findById(id).orElseThrow();
        List<User> students = course.getStudents();
        return userMapper.toSimpleDTOs(students);
    }

    public void uploadImage(long courseId, URI location, InputStream inputStream, long size) {

        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setImage(location.toString());
        course.setImageCourse(BlobProxy.generateProxy(inputStream, size));
        courseRepository.save(course);
    }
}