package es.dws.aulavisual.courses;

import es.dws.aulavisual.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseManager {

    private final Map <Long, Course> courseList = new HashMap <>();
    private long nextId;

    public CourseManager() {

        List <Long> loluserIds = new ArrayList <>();
        loluserIds.add(Long.parseLong("2"));
        loluserIds.add(Long.parseLong("3"));

        List <Module> lolModules = new ArrayList <>();
        Module lolModule1 = new Module(0, "Intro");
        lolModules.add(lolModule1);

        Module lolModule2 = new Module(1, "Champions");
        lolModules.add(lolModule2);

        Module lolModule3 = new Module(2, "Delete_me");
        lolModules.add(lolModule3);

        Course lolCourse = new Course(0, "League of Legends", "Aprende a jugar al LOL", 1, loluserIds, lolModules);
        courseList.put(0L, lolCourse);

        List <Long> padelUserIds = new ArrayList <>();
        padelUserIds.add(Long.parseLong("3"));
        padelUserIds.add(Long.parseLong("4"));
        Course padelCourse = new Course(1, "Paddle", "Star having fun while exercising", 1, padelUserIds, new ArrayList <>());
        courseList.put(1L, padelCourse);

        List <Long> cookingUserIds = new ArrayList <>();
        cookingUserIds.add(Long.parseLong("2"));
        cookingUserIds.add(Long.parseLong("4"));
        Course cookingUourse = new Course(2, "Cooking", "Learn how to prepare easy yet delicious meals", 1, cookingUserIds, new ArrayList <>());
        courseList.put(2L, cookingUourse);
        this.nextId = 3;
    }

    public void createCourse(String name, String description, long teacherId, List <Module> modules) {

        long id = nextId;
        this.nextId++;
        List <Long> userIds = new ArrayList <>();
        Course course = new Course(id, name, description, teacherId, userIds, modules);
        courseList.put(id, course);
    }

    public void addModule(long courseId, String name, MultipartFile module) {

        try {

            Course course = getCourse(courseId);
            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Files.createDirectories(coursePath);
            Path modulePath = coursePath.resolve("module-" + course.getNumberModules() + "-" + name + ".md");
            module.transferTo(modulePath);
            course.addModule(new Module(course.getNumberModules(), name));
        }catch (Exception e) {

            System.out.println("Error saving madule: " + e.getMessage());
        }
    }

    public Course getCourse(long courseId) {

        return courseList.get(courseId);
    }

    public List <Course> getCourses() {

        return new ArrayList <>(courseList.values());
    }

    public boolean userInCourse(long courseId, long userId) {
        Course course = courseList.get(courseId);
        return course.getUserIds().contains(userId);
    }

    public void removeModule(long courseId, long moduleId) {
        Course course = courseList.get(courseId);
        Module module = course.getModuleById(moduleId);
        if (module != null) {

            try {
                Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
                Path modulePath = coursePath.resolve("module" + moduleId + "-" + module.getName() + ".md");
                Files.deleteIfExists(modulePath);
            } catch (IOException e) {
                System.out.println("Error deleting module file: " + e.getMessage());
            }
            course.removeModule(module);
        }
    }

    public ResponseEntity<Object> viewCourse(long courseId, long id) {
        Course course = courseList.get(courseId);
        Module module = course.getModuleById(id);

        try {

            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Path modulePath = coursePath.resolve("module" + id + "-" + module.getName() + ".md");
            Resource content = new UrlResource(modulePath.toUri());
            if (!Files.exists(modulePath)) {

                return ResponseEntity.status(404).body("Module not found2");
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/markdown").body(content);
        }catch (Exception e) {

            return ResponseEntity.status(404).body("Module not found3");
        }
    }

    public void removeCourse(long courseId) {
        Course course = courseList.get(courseId);
        List <Module> modules = course.getModules();
        deleteAllModules(courseId);
        try {
            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Files.deleteIfExists(coursePath);
        } catch (IOException e) {
            System.out.println("Error deleting course file: " + e.getMessage());
        }
        courseList.remove(courseId);
    }

    public void deleteAllModules(Long courseId) {
        Course course = courseList.get(courseId);
        List <Module> modules = course.getModules();
        for (Module module : modules) {
            try {
                Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
                Path modulePath = coursePath.resolve("module" + module.getId() + "-" + module.getName() + ".md");
                Files.deleteIfExists(modulePath);
            } catch (IOException e) {
                System.out.println("Error deleting module file: " + e.getMessage());
            }
        }
        course.getModules().clear();
    }
}