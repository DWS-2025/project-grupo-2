package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.*;
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

    public CourseDTO assignTeacher(long id, CourseDTO courseDTO) {

        Course course = courseRepository.findById(courseDTO.id()).orElseThrow();
        User teacher = userService.findById(id);
        if(teacher.getRole() != 1 || teacher.getCourseTeaching() != null) {
            throw new IllegalArgumentException("Teacher is not valid");
        }
        course.setTeacher(teacher);
        courseRepository.save(course);
        userService.addCourseToTeacher(teacher.getId(), course);
        return courseMapper.toDTO(course);
    }

    public CourseDTO saveDTO(CourseDTO courseDTO) {

        Course course = courseMapper.toDomain(courseDTO);
        return courseMapper.toDTO(save(course));
    }

    public Course save(Course course) {

        return courseRepository.save(course);
    }

    public void addUserToCourse(long courseId, UserDTO userDTO) {

        // Course course = courseMapper.toDomain(courseDTO);
        Course course = findById(courseId);
        //User user = userMapper.toDomain(userDTO);
        User user = userService.findById(userDTO.id());
        course.getStudents().add(user);
        //user.getCourses().add(course);
        //userService.save(user);
        courseRepository.save(course);
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
        if (course.getTeacher() != null) {
            course.getTeacher().setCourseTeaching(null);
        }
        courseRepository.deleteById(courseId);
        System.out.println("Hola");
    }

    void removeCourseFromTeacher(User teacher, Course course) {

        teacher.setCourseTeaching(null);
    }

    public ResponseEntity<Object> loadImage(Long id){

        Course course = courseRepository.findById(id).orElseThrow();
        try {

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

    public void uploadImage(long courseId, String location, InputStream inputStream, long size) {

        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setImage(location);
        course.setImageCourse(BlobProxy.generateProxy(inputStream, size));
        courseRepository.save(course);
    }

    public boolean updateRole(UserDTO userDTO, int newRole) {

        User user = userService.findById(userDTO.id());
        if(newRole >= 0 && newRole <= 2) {

            user.setRole(newRole);
            if(newRole == 0 || newRole == 1) {

                user.getCourses().forEach(course -> {
                    course.getStudents().remove(user);
                    courseRepository.save(course);
                });
                user.clearCourses();
            }
            userService.save(user);
            return true;
        }
        return false;
    }
}