package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.DTO.*;
import es.dws.aulavisual.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/")
public class CourseRestController {

    private final CourseService courseService;

    public CourseRestController(CourseService courseService) {

        this.courseService = courseService;
    }

    @GetMapping("courses/")
    public ResponseEntity<Object> courses() {

        try{
            List<CourseDTO> courses = courseService.getCourses();
            return ResponseEntity.ok(courses);
        }catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("user/{id}/courses/")
    public ResponseEntity<List <CourseInfoDTO>> coursesOfUser(@PathVariable Long id) {

        List<CourseInfoDTO> courses = courseService.courseInfoOfUser(id);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("course/{id}/")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {

        CourseDTO course = courseService.findByIdDTO(id);
        return ResponseEntity.ok(course);
    }

    @PostMapping("courses/")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {

        CourseDTO createCourseDTO = courseService.saveDTO(courseDTO);
        return ResponseEntity.ok(createCourseDTO);
    }

    @GetMapping("course/{id}/users/")
    public ResponseEntity<List<UserSimpleDTO>> getUsersInCourse(@PathVariable Long id) {

        List<UserSimpleDTO> users = courseService.getAllStudentsfromCourse(id);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("course/{id}/")
    public ResponseEntity<CourseDTO> deleteCourse(@PathVariable Long id) {

        CourseDTO courseDTO = courseService.findByIdDTO(id);
        courseService.deleteCourse(id);
        return ResponseEntity.ok(courseDTO);
    }

    @PutMapping("course/{id}/image/")
    public ResponseEntity<Object> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {

        try {
            String location = fromCurrentRequest().path("").buildAndExpand(id).toUri().getPath();
            courseService.uploadImage(id, location, image.getInputStream(), image.getSize());
            return ResponseEntity.created(URI.create(location)).body(location);
        } catch (IOException e){
            return ResponseEntity.badRequest().body("Error uploading image: " + e.getMessage());
        }
    }

    @GetMapping("course/{id}/image/")
    public ResponseEntity<Object> loadImage(@PathVariable Long id) {

        try{
            return courseService.loadImage(id);
        } catch (NoSuchElementException e){
            return ResponseEntity.badRequest().body("Error loading image: " + e.getMessage());
        }
    }


    @PutMapping("course/{id}/")
    public ResponseEntity<Object> addTeacherToCourse(@PathVariable Long id, @RequestBody TeacherInfoDTO teacherInfoDTO) {

        CourseDTO courseDTO = courseService.findByIdDTO(id);
        try{
            CourseDTO newCourseDTO = courseService.assignTeacher(teacherInfoDTO.id(), courseDTO);
            return ResponseEntity.ok(newCourseDTO);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}