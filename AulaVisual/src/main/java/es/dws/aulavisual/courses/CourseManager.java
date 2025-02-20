package es.dws.aulavisual.courses;

import es.dws.aulavisual.Paths;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
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
        Module lolModule1 = new Module(0, "Intro", "/files/courses/course-0/module-0-Intro.md");
        lolModules.add(lolModule1);

        Module lolModule2 = new Module(1, "Champions", "/files/courses/course-0/module-1-Champions.md");
        lolModules.add(lolModule2);

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

    public void addModule(long courseId, Module module) {

        Course course = courseList.get(courseId);
        course.addModule(module);
    }

    public Course getCourse(long courseId) {

        return courseList.get(courseId);
    }

    public List <Course> getCourses() {

        return new ArrayList <>(courseList.values());
    }
}