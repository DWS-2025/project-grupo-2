package es.dws.aulavisual;

import es.dws.aulavisual.DTO.CourseDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.CourseMapper;
import es.dws.aulavisual.Mapper.UserMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.service.CourseService;
import es.dws.aulavisual.service.ModuleService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import es.dws.aulavisual.service.UserService;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.NoSuchElementException;

import org.springframework.mock.web.MockMultipartFile;

@Service
public class SampleDataService {

    private final CourseService courseService;
    private final UserService userService;
    private final ModuleService moduleService;
    private final CourseMapper courseMapper;

    public SampleDataService(CourseService courseService, UserService userService, ModuleService moduleService, CourseMapper courseMapper) {

        this.courseService = courseService;
        this.userService = userService;
        this.moduleService = moduleService;
        this.courseMapper = courseMapper;
    }

    @PostConstruct
    public void init() {

        try {
            userService.saveDTO("teacher1", "teacher1", "teacher1", "teacher1", "Noxus", "TEACHER");
            userService.saveDTO("teacher2", "teacher2", "teacher2", "teacher2", "Piltover", "TEACHER");
            userService.saveDTO("teacher3", "teacher3", "teacher3", "teacher3", "Zaun", "TEACHER");

            userService.saveDTO("asd", "asd", "asd", "asd", "Zaun", "ADMIN");
            userService.saveDTO("test2", "test2", "test2", "test2", "Zaun", "USER");
            userService.saveDTO("test3", "test3", "test3", "test3", "Zaun", "USER");
            userService.saveDTO("test4", "test4", "test4", "test4", "Zaun", "USER");

            Course course1 = new Course("League of Legends", "Aprende a jugar al LOL", "Haz una redacción sobre el control de oleadas", convertPNGToMultipart("files/courses/course-0/img.png"));
            course1.setImage("/api/course/1/image");
            courseService.save(course1);
            Course course2 = new Course("Padel", "Comienza a disfrutar de hacer ejercicio", "Explica las reglas del padel", convertPNGToMultipart("files/courses/course-1/img.png"));
            course2.setImage("/api/course/2/image");
            courseService.save(course2);
            Course course3 = new Course("Recetas de Cocina", "Aprende a cocinar recetas increíblemente sabrosas", "Haz una receta que incluya huevos, pasta y tomate", convertPNGToMultipart("files/courses/course-2/img.png"));
            course3.setImage("/api/course/3/image");
            courseService.save(course3);


            UserDTO teacher1 = userService.findByUserName("teacher1");
            UserDTO teacher2 = userService.findByUserName("teacher2");
            UserDTO teacher3 = userService.findByUserName("teacher3");
            courseService.assignTeacher(teacher1.id(), courseMapper.toDTO(course1));
            courseService.assignTeacher(teacher2.id(), courseMapper.toDTO(course2));
            courseService.assignTeacher(teacher3.id(), courseMapper.toDTO(course3));
            courseService.addUserToCourse(course1.getId(), userService.findByUserName("test2"));
            courseService.addUserToCourse(course2.getId(), userService.findByUserName("test3"));
            courseService.addUserToCourse(course3.getId(), userService.findByUserName("test4"));

            CourseDTO course1DTO = courseMapper.toDTO(course1);
            CourseDTO course2DTO = courseMapper.toDTO(course2);
            CourseDTO course3DTO = courseMapper.toDTO(course3);
            moduleService.save(course1DTO, "Intro", 1, convertMDToMultipart("files/courses/course-0/module0-Intro.md"));
            moduleService.save(course1DTO, "Campeones", 2, convertMDToMultipart("files/courses/course-0/module1-Champions.md"));
            moduleService.save(course1DTO, "Delete Me", 3, convertMDToMultipart("files/courses/course-0/module2-Delete_me.md"));
            moduleService.save(course2DTO, "Delete Me", 1, convertMDToMultipart("files/courses/course-0/module2-Delete_me.md"));
            moduleService.save(course3DTO, "Delete Me", 1, convertMDToMultipart("files/courses/course-0/module2-Delete_me.md"));


            for(int i = 0; i < 50; i++){

                userService.saveDTO("testPage" + i, "testPage" + i, "testPage" + i, "testPage" +i, "Zaun", "USER");
            }
        }catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println("Error creating sample data");
        }
    }

    private MultipartFile convertPNGToMultipart(String filePath) {

        try{
            File file = new File(filePath);
            FileInputStream input = new FileInputStream(file);

            return new MockMultipartFile(
                    file.getName(),            // File name
                    file.getName(),            // Original name file
                    "image/png",               // Content type (adjust by the image format)
                    input                      // File content
            );
        }catch (Exception e){

            throw new RuntimeException("Error converting file to Multipart", e);
        }
    }

    private MultipartFile convertMDToMultipart(String filePath) {

        try{
            File file = new File(filePath);
            FileInputStream input = new FileInputStream(file);

            return new MockMultipartFile(
                    file.getName(),            // File name
                    file.getName(),            // Original name file
                    "text/markdown",           // Content type (adjust by the image format)
                    input                      // File content
            );
        }catch (Exception e){

            throw new RuntimeException("Error converting file to Multipart", e);
        }
    }
}
