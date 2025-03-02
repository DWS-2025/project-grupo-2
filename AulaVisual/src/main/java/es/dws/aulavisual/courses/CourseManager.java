package es.dws.aulavisual.courses;

import es.dws.aulavisual.Paths;
import es.dws.aulavisual.submissions.Submission;
import es.dws.aulavisual.users.User;
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

        Course lolCourse = new Course(0, "League of Legends", "Aprende a jugar al LOL", 1, loluserIds, lolModules, "Haz una redacción sobre el control de oleadas");
        courseList.put(0L, lolCourse);

        List <Long> padelUserIds = new ArrayList <>();
        padelUserIds.add(Long.parseLong("3"));
        padelUserIds.add(Long.parseLong("4"));
        Course padelCourse = new Course(1, "Paddle", "Star having fun while exercising", 1, padelUserIds, new ArrayList <>(), "Explica las reglas del padel");
        courseList.put(1L, padelCourse);

        List <Long> cookingUserIds = new ArrayList <>();
        cookingUserIds.add(Long.parseLong("2"));
        cookingUserIds.add(Long.parseLong("4"));
        Course cookingCourse = new Course(2, "Cooking", "Learn how to prepare easy yet delicious meals", 1, cookingUserIds, new ArrayList <>(), "Haz una receta que incluya huevos, pasta y tomate");
        courseList.put(2L, cookingCourse);
        this.nextId = 3;
    }

    public void createCourse(String name, String description, long teacherId, List <Module> modules, String task) {

        long id = nextId;
        this.nextId++;
        List <Long> userIds = new ArrayList <>();
        Course course = new Course(id, name, description, teacherId, userIds, modules, task);
        courseList.put(id, course);
    }

    public boolean addModule(long courseId, String name, MultipartFile module) {

        try {

            Course course = getCourse(courseId);
            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Files.createDirectories(coursePath);
            Path modulePath = coursePath.resolve("module" + course.getNumberModules() + "-" + name + ".md");
            Files.createFile(modulePath);
            module.transferTo(modulePath);
            course.addModule(new Module(course.getNumberModules(), name));
            return true;
        }catch (Exception e) {

            System.out.println("Error saving madule: " + e.getMessage());
        }
        return false;
    }

    public Course getCourse(long courseId) {

        return courseList.get(courseId);
    }

    public List <Course> getCourses() {

        return new ArrayList <>(courseList.values());
    }

    public boolean userInCourse(long courseId, long userId) {
        Course course = courseList.get(courseId);
        return course.getUserIds().contains(userId) || course.getTeacherId() == userId;
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

    public Course removeCourse(long courseId) {
        deleteAllModules(courseId);
        try {
            Path imagePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId).resolve("img.png");
            Files.deleteIfExists(imagePath);
            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Files.deleteIfExists(coursePath);
        } catch (IOException e) {
            System.out.println("Error deleting course file: " + e.getMessage());
        }
        return courseList.remove(courseId);
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

    public void addImage(MultipartFile image) {

        try {

            long courseId = this.nextId;
            courseId--;
            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Files.createDirectories(coursePath);
            Path imgPath = coursePath.resolve("img.png");
            image.transferTo(imgPath);
        }catch (Exception e) {

            System.out.println("Error saving img: " + e.getMessage());
        }
    }

    public Path getImage(long courseId) {

        try {

            Path coursePath = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
            Path imgPath = coursePath.resolve("img.png");
            if (Files.exists(imgPath)) {

                return imgPath;
            }
        }catch (Exception e) {

            System.out.println("Error getting img: " + e.getMessage());
        }
        return null;
    }

    public boolean addSubmission(long courseId, User user){

        Course course = courseList.get(courseId);
        if(course.userMadeSubmission(user.getId())) {
            return false;
        }else {

            Submission submission = new Submission(courseId, user);
            course.addSubmission(submission);
            return true;
        }
    }

    public boolean userMadeSubmission(long courseId, long userId) {
        Course course = courseList.get(courseId);
        return course.userMadeSubmission(userId);
    }

    public String getTask(long courseId) {
        Course course = courseList.get(courseId);
        return course.getTask();
    }

    public long getTeacherId(long courseId) {

        Course course = courseList.get(courseId);
        return course.getTeacherId();
    }

    public void gradeSubmission(long courseId, long studentId, float grade) {

        Course course = courseList.get(courseId);
        Submission submission = course.getSubmission(studentId);
        submission.setGrade(grade);
    }

    public float getGrade(long courseId, long userId) {

        Course course = courseList.get(courseId);
        Submission submission = course.getSubmission(userId);
        return submission.getGrade();
    }

    public boolean isgraded(long courseId, long userId) {

        Course course = courseList.get(courseId);
        Submission submission = course.getSubmission(userId);
        return submission.isGraded();
    }

    public void deleteSubmission(long courseId, long studentId) {

        Course course = courseList.get(courseId);
        Submission submission = course.getSubmission(studentId);
        course.removeSubmission(submission);
    }
}