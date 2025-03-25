package es.dws.aulavisual.service;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.CourseMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.repository.CourseRepository;
import es.dws.aulavisual.model.User;
import jakarta.transaction.Transactional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.sql.Blob;

import org.springframework.http.ResponseEntity;
import java.util.List;

import es.dws.aulavisual.Mapper.UserMapper;

@Service
@Transactional
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

    public void addUserToCourse(CourseDTO courseDTO, UserDTO user) {

        Course course = courseMapper.toDomain(courseDTO);
        course.getStudents().add(userMapper.toDomain(user));
        //user.getCourses().add(course);
        userService.saveDTO(userMapper.toDomain(user));
        courseRepository.save(course);
    }

    void removeUserFromCourse(Course course, User user) {

        course.getStudents().remove(user);
    }

    public List<Course> getCourses() {

        return courseRepository.findAll();
    }

    public CourseDTO findByIdDTO(long id) {

        return courseMapper.toDTO(findById(id));
    }

    Course findById(long id) {

        return courseRepository.findById(id).orElseThrow();
    }

    public boolean userIsInCourse(UserDTO userDTO, CourseDTO courseDTO) {

        User user = userMapper.toDomain(userDTO);
        Course course = courseMapper.toDomain(courseDTO);
        return course.getTeacher().equals(user)|| course.getStudents().contains(user);
    }

    public void deleteCourse(CourseDTO courseDTO) {

        Course course = courseMapper.toDomain(courseDTO);
        userService.removeAllUsersFromCourse(course);
        courseRepository.delete(course);
    }

    public ResponseEntity<Object> loadImage(CourseDTO courseDTO){

        try {

            Course course = courseMapper.toDomain(courseDTO);
            Blob image = course.getImage();
            if(image == null) {

                System.out.println("course has no image");
                return null;
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

    public List<Course> courseOfUser(UserDTO userDTO) {

        User user = userMapper.toDomain(userDTO);
        List<Course> normalCourses = courseRepository.searchCoursesByStudentsContaining(user);
        normalCourses.addAll(courseOfTeacher(userDTO));
        return normalCourses;
    }

    public List<Course> notCourseOfUser(UserDTO userDTO) {

        User user = userMapper.toDomain(userDTO);
        List<Course> notCourses = courseRepository.searchCoursesByStudentsNotContaining(user);
        notCourses.removeAll(courseOfTeacher(userDTO));
        return notCourses;
    }

    public List<Course> courseOfTeacher(UserDTO userDTO) {

        User user = userMapper.toDomain(userDTO);
        return courseRepository.searchCoursesByTeacherId(user.getId());
    }
}