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
import java.sql.Blob;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
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
        if(!teacher.getRole().equals("TEACHER") || teacher.getCourseTeaching() != null) {
            throw new IllegalArgumentException("Teacher is not valid");
        }
        course.setTeacher(teacher);
        courseRepository.save(course);
        userService.addCourseToTeacher(teacher.getId(), course);
        return courseMapper.toDTO(course);
    }

    public CourseDTO saveDTO(CourseDTO courseDTO) {

        if(userService.hasRoleOrHigher("ADMIN")){

            Course course = courseMapper.toDomain(courseDTO);
            course.setTeacher(null);
            course.setStudents(new ArrayList <>());
            course.setImage(null);
            return courseMapper.toDTO(save(course));
        }else{
            throw new RuntimeException("No tienes permisos para esto");
        }
    }

    public Course save(Course course) {

        return courseRepository.save(course);
    }

    public void addUserToCourse(long courseId, UserDTO userDTO) {

        // The only way to access this method is via protected endpoints in the API and Controllers
        User user = userService.findById(userDTO.id());
        if(user.getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("No puedes añadir un admin a un curso");
        }
        if(user.getRole().equals("TEACHER")) {
            throw new IllegalArgumentException("No puedes añadir un profesor a un curso");
        }
        Course course = findById(courseId);
        course.getStudents().add(user);
        courseRepository.save(course);
    }


    public List<CourseDTO> getCourses() {

        // Only the admin is able to see all the courses
        if(userService.hasRoleOrHigher("ADMIN")) {
            return courseMapper.toDTOs(courseRepository.findAll());
        }
        throw new RuntimeException("No tienes permisos para ver los cursos");
    }

    public CourseDTO findByIdDTO(long id) {

        User user = userService.getLoggedUser();
        if(userService.hasRoleOrHigher("USER") && (user.getRole().equals("ADMIN") || userIsInCourse(user.getId(), id))) {
            return courseMapper.toDTO(findById(id));
        }
        throw new RuntimeException("No tienes permisos para esto");
    }

    Course findById(long id) {

        return courseRepository.findById(id).orElseThrow();
    }

    public boolean userIsInCourse(long userId, long courseId) {

        User loggedUser = userService.getLoggedUser();
        Course course = courseRepository.findById(courseId).orElseThrow();
        if(userService.hasRoleOrHigher("ADMIN") || loggedUser.getId() == userId || loggedUser.equals(course.getTeacher())) {    //If the user want to see his own course
            User user = userService.findById(userId);
            return course.getTeacher().equals(user)|| course.getStudents().contains(user);
        }
        throw new RuntimeException("No tienes permisos para esto");
    }

    public CourseDTO deleteCourse(long courseId) {

        if(!userService.hasRoleOrHigher("ADMIN")) {
            throw new RuntimeException("No tienes permisos para esto");
        }
        Course course = courseRepository.findById(courseId).orElseThrow();
        if (course.getTeacher() != null) {
            course.getTeacher().setCourseTeaching(null);
        }
        courseRepository.deleteById(courseId);
        return courseMapper.toDTO(course);
    }

    void removeCourseFromTeacher(User teacher, Course course) {

        User admin = userService.getLoggedUser();
        if(!admin.getRole().equals("ADMIN")) {
            throw new RuntimeException("No tienes permisos para esto");
        }
        teacher.setCourseTeaching(null);
    }

    public ResponseEntity<Object> loadImage(Long id){

        // If you no are logged in you can not see any course image (I don't want you to)
        if(!userService.hasRoleOrHigher("USER")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
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

        User loggedUser = userService.getLoggedUser();
        if(!userService.hasRoleOrHigher("USER")) {
            throw new RuntimeException("Primero debes iniciar sesión");
        }

        if (loggedUser.getRole().equals("TEACHER")){
            throw new RuntimeException("No puedes ver los cursos siendo profesor");
        }
        User user = userService.findById(id);
        List<Course> normalCourses = courseRepository.searchCoursesByStudentsContaining(user);
        return courseMapper.toDTOs(normalCourses);
    }

    public List<CourseDTO> notCourseOfUser(UserDTO userDTO) {

        User loggedUser = userService.getLoggedUser();
        if(!userService.hasRoleOrHigher("USER")) {
            throw new RuntimeException("Primero debes iniciar sesión");
        }

        if(loggedUser.getRole().equals("TEACHER")){
            throw new RuntimeException("No puedes ver los cursos siendo profesor");
        }
        User user = userService.findById(userDTO.id());
        List<Course> notCourses = courseRepository.searchCoursesByStudentsNotContaining(user);
        if (user.getRole().equals("TEACHER")) {
            notCourses.removeAll(courseOfTeacher(userDTO));
        }
        // ToDTOs is broken
        return courseMapper.toDTOs(notCourses);
    }

    List<Course> courseOfTeacher(UserDTO userDTO) {

        // Already checked in the functions that call this one
        User user = userService.findById(userDTO.id());
        return courseRepository.searchCoursesByTeacherId(user.getId());
    }

    public List<CourseInfoDTO> courseInfoOfUser(Long id) {

        //Only used in rest controller
        User loggedUser = userService.getLoggedUser();
        if(!userService.hasRoleOrHigher("ADMIN") && loggedUser.getId() != id) {
            throw new RuntimeException("No tienes permiso para ver otros cursos");
        }
        User user = userService.findById(id);
        if(user.getRole().equals("TEACHER")) {
            return courseMapper.toInfoDTOs(courseOfTeacher(userMapper.toDTO(user)));

        }
        List<Course> normalCourses = courseRepository.searchCoursesByStudentsContaining(user);
        return courseMapper.toInfoDTOs(normalCourses);
    }

    public List<UserSimpleDTO> getAllStudentsfromCourse(Long id) {

        //Only used in rest controller
        User loggedUser = userService.getLoggedUser();
        if(!userService.hasRoleOrHigher("ADMIN") && loggedUser.getCourseTeaching().getId() != id) {
            throw new RuntimeException("No tienes permiso para ver otros cursos");
        }
        Course course = courseRepository.findById(id).orElseThrow();
        List<User> students = course.getStudents();
        return userMapper.toSimpleDTOs(students);
    }

    public void uploadImage(long courseId, String location, InputStream inputStream, long size) {

        if(!userService.hasRoleOrHigher("ADMIN")) {
            throw new RuntimeException("No tienes permisos para esto");
        }
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setImage(location);
        course.setImageCourse(BlobProxy.generateProxy(inputStream, size));
        courseRepository.save(course);
    }

public boolean updateRole(UserDTO userDTO, int newRole) {

        User loggedUser;
        try {
            loggedUser = userService.getLoggedUser();
        } catch (RuntimeException e) {
            loggedUser = null;
        }

        if(loggedUser == null || loggedUser.getId() == userDTO.id()) { //Lower level user or admin trying to update his own role
            return false;
        }
        if(userService.hasRoleOrHigher("ADMIN")) {


            User user = userService.findById(userDTO.id());
            if(newRole >= 0 && newRole <= 2) {

                switch (newRole) {
                    case 0 -> user.setRole("ADMIN");
                    case 1 -> user.setRole("TEACHER");
                    case 2 -> user.setRole("USER");
                }
                if(newRole == 0 || newRole == 1) {

                    user.getCourses().forEach(course -> {
                        course.getStudents().remove(user);
                        courseRepository.save(course);
                    });
                    user.clearCourses();
                }

                if(newRole == 2 && user.getCourseTeaching() != null) {

                    Course courseTeaching = user.getCourseTeaching();
                    courseTeaching.setTeacher(null);
                    save(courseTeaching);
                    user.setCourseTeaching(null);

                }
                userService.save(user);
                return true;
            }
        }
        return false;
    }

    public UserDTO updateCourseRole(UserDTO userDTO, int newRole, UserCreationDTO userCreationDTO){

        //Only used in rest controller
        if(updateRole(userDTO, newRole)){

            if(!(userService.findById(userDTO.id()).getRole().equals("ADMIN") || userService.findById(userDTO.id()).getRole().equals("TEACHER"))){

                for(int i = 0; i < userCreationDTO.userDTO().courses().size(); i++) {

                    addUserToCourse(userCreationDTO.userDTO().courses().get(i).id(), userDTO);
                }
            }

            return userService.findByIdDTO(userDTO.id());
        }
        return userDTO;
        //throw new RuntimeException("Usuario, rol inválidos o no se ha podido actualizar el rol");
    }
}

