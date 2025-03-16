package es.dws.aulavisual;

import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.User;
import es.dws.aulavisual.service.CourseService;
import es.dws.aulavisual.service.ModuleService;
import org.springframework.web.multipart.MultipartFile;
import es.dws.aulavisual.service.UserService;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import org.springframework.mock.web.MockMultipartFile;

@Service
public class SampleDataService {

    private final CourseService courseService;
    private final UserService userService;
    private final ModuleService moduleService;

    public SampleDataService(CourseService courseService, UserService userService, ModuleService moduleService) {

        this.courseService = courseService;
        this.userService = userService;
        this.moduleService = moduleService;
    }

    @PostConstruct
    public void init() {

        User teachcer1 = new User("teacher1", "teacher1", "teacher1", "teacher1", 1);
        User teachcer2 = new User("teacher2", "teacher2", "teacher2", "teacher2", 1);
        User teachcer3 = new User("teacher3", "teacher3", "teacher3", "teacher3", 1);
        userService.save("asd", "asd", "asd", "asd", 0);
        userService.save(teachcer1);
        userService.save(teachcer2);
        userService.save(teachcer3);
        userService.save("test2", "test2", "test2", "test2", 2);
        userService.save("test3", "test3", "test3", "test3", 2);
        userService.save("test4", "test4", "test4", "test4", 2);

        Course course1 = new Course("League of Legends", "Aprende a jugar al LOL", teachcer1, "Haz una redacción sobre el control de oleadas", convertPNGToMultipart("files/courses/course-0/img.png"));
        courseService.save(course1);
        Course course2 = new Course("Padel", "Comienza a disfrutar de hacer ejercicio", teachcer2, "Explica las reglas del padel", convertPNGToMultipart("files/courses/course-1/img.png"));
        courseService.save(course2);
        Course course3 = new Course("Recetas de Cocina", "Aprende a cocinar recetas increíblemente sabrosas", teachcer3, "Haz una receta que incluya huevos, pasta y tomate", convertPNGToMultipart("files/courses/course-2/img.png"));
        courseService.save(course3);

        courseService.addUserToCourse(course1, userService.findByUserName("test2").get());
        courseService.addUserToCourse(course1, userService.findByUserName("test3").get());
        courseService.addUserToCourse(course2, userService.findByUserName("test4").get());

        moduleService.save(course1, "Intro", 1, convertMDToMultipart("files/courses/course-0/module0-Intro.md"));
        moduleService.save(course1, "Campeones", 2, convertMDToMultipart("files/courses/course-0/module1-Champions.md"));
        moduleService.save(course1, "Delete Me", 3, convertMDToMultipart("files/courses/course-0/module2-Delete_me.md"));

    }

    private MultipartFile convertPNGToMultipart(String filePath) {

        try{
            File file = new File(filePath);
            FileInputStream input = new FileInputStream(file);

            return new MockMultipartFile(
                    file.getName(),            // Nombre del archivo
                    file.getName(),            // Nombre original del archivo
                    "image/png",              // Tipo de contenido (ajustar según el formato de la imagen)
                    input                      // Contenido del archivo
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
                    file.getName(),            // Nombre del archivo
                    file.getName(),            // Nombre original del archivo
                    "text/markdown",              // Tipo de contenido (ajustar según el formato de la imagen)
                    input                      // Contenido del archivo
            );
        }catch (Exception e){

            throw new RuntimeException("Error converting file to Multipart", e);
        }
    }
}
