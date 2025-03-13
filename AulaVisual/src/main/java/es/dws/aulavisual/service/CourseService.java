package es.dws.aulavisual.service;

import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.repository.CourseRepository;
import es.dws.aulavisual.model.User;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;


    public CourseService(CourseRepository courseRepository, UserService userService) {

        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    public void save(Course course) {

        courseRepository.save(course);
    }

    public void addUserToCourse(Course course, User user) {

        course.getStudents().add(user);
        courseRepository.save(course);
        //user.getCourses().add(course);
        userService.save(user);
    }

    public List<Course> getCourses() {

        return courseRepository.findAll();
    }

    public Optional<Course> findById(long id) {

        return courseRepository.findById(id);
    }

    public boolean userIsInCourse(User user, Course course) {

        return  course.getTeacherId() == user.getId()|| course.getStudents().contains(user);
    }

    public void deleteCourse(Course course) {

        userService.removeAllUsersFromCourse(course);
        courseRepository.delete(course);
    }

    public ResponseEntity<Object> loadImage(Course course){

        try {

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

    public List<Course> courseOfUser(User user) {

        List<Course> normalCourses = courseRepository.searchCoursesByStudentsNotContaining(user);
        normalCourses.addAll(courseOfTeacher(user));
        return normalCourses;
    }

    public List<Course> notCourseOfUser(User user) {

        return courseRepository.searchCoursesByStudentsNotContaining(user);
    }

<<<<<<< Updated upstream
//    public Module getFirstModule(Course course) {
//
//        return
//    }
=======
    public List<Course> courseOfTeacher(User user) {

        return courseRepository.searchCoursesByTeacherId(user.getId());
    }
>>>>>>> Stashed changes
}