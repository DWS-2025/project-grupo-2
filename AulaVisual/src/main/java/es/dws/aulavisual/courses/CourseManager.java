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

        List<Long> loluserIds = new ArrayList <>();
        loluserIds.add(Long.parseLong("2"));
        loluserIds.add(Long.parseLong("3"));
        List<Module> lolModules = new ArrayList <>();
        Course lolCourse = new Course(0, "League of Legends", "Learn to play League of Legends", 1, loluserIds, new ArrayList <>());

        List<Long> padelUserIds = new ArrayList <>();
        padelUserIds.add(Long.parseLong("3"));
        padelUserIds.add(Long.parseLong("4"));
        Course padelCourse = new Course(0, "Paddle", "Star having fun while exercising", 1, padelUserIds, new ArrayList <>());

        List<Long> cookingUserIds = new ArrayList <>();
        cookingUserIds.add(Long.parseLong("2"));
        cookingUserIds.add(Long.parseLong("4"));
        Course cookingUourse = new Course(0, "Cooking", "Learn how to prepare easy yet delicious meals", 1, cookingUserIds, new ArrayList <>());
//        loadNextId();
//        loadCourseFromDisk();
    }

    private void loadNextId() {

        try {

            Reader reader = new FileReader(Paths.CURRENTCOURSEIDPATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            nextId = Long.parseLong(line);
            reader.close();
        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private void loadCourseFromDisk() {

        try {

            Reader reader = new FileReader(Paths.COURSESMAPPATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while (line != null) {

                String[] parts = line.split(";");
                long nextId = Long.parseLong(parts[0]);
                List <Long> userIds = new ArrayList <>();
                for(int i = 4; i < parts.length; i++) {

                    userIds.add(Long.parseLong(parts[i]));
                }
                List <Module> modules = new ArrayList <>();
//                readModules(nextId, modules);
                Course course = new Course(nextId, parts[1], parts[2], Long.parseLong(parts[3]), userIds, modules);
                courseList.put(nextId, course);
                line = bufferedReader.readLine();
            }
            reader.close();
        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void createCourse(String name, String description, long teacherId, List <Module> modules) {

        long id = nextId;
        this.nextId++;
        List <Long> userIds = new ArrayList <>();
        Course course = new Course(id, name, description, teacherId, userIds, modules);
        courseList.put(id, course);
    }

//    private void saveNextId() {
//
//        try {
//
//            this.nextId++;
//            Writer writer = new FileWriter(Paths.CURRENTCOURSEIDPATH);
//            String line = Long.toString(nextId);
//            writer.write(line);
//            writer.close();
//
//        }catch (IOException e) {
//
//            throw new RuntimeException(e);
//        }
//    }

//    private void saveCourseInDisk(long courseId, Course course, MultipartFile ...modulesContent) {
//
//        try {
//
//            Writer writer = new FileWriter(Paths.COURSESMAPPATH, true);
//            StringBuilder line = new StringBuilder(courseId + ";" + course.getName() + ";" + course.getDescription() + ";" + course.getTeacherId());
//            List <Long> userIds = course.getUserIds();
//            for(long userId : userIds) {
//
//                line.append(";").append(userId);
//            }
//            line.append("\n");
//            writer.write(line.toString());
//            writer.close();
//
//            Path folder = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
//            Map<Long, Module> modules = course.getModules();
//
//            for (int i = 0; i < modules.size(); i++) {
//
//                Writer moduleWriter = new FileWriter(folder.resolve("module-" + courseId + "." + i +".txt").toFile());
//                Module module = modules.get(Integer.toUnsignedLong(i));
//                moduleWriter.write(module.getId() + ";" + module.getName() + ";" + module.getDescription());
//                moduleWriter.close();
//            }
//
//        }catch (IOException e) {
//
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Course getCourse(long id) {
//
//        return courseList.get(id);
//    }
//
//    public List <Course> getCourses() {
//
//        return new ArrayList <>(courseList.values());
//    }

//    private void readModules(long courseId, Map<Long, Module> modules) {
//
//        Path folder = Paths.COURSEMODULESPATH.resolve("course-" + courseId);
//        for (int i = 0; i < folder.getNameCount()/2; i++) {
//
//            try {
//
//                Path modulePath = folder.resolve("module-" + courseId + "." + i +".md");
//                Reader reader = new FileReader(folder.resolve("module-" + courseId + "." + i +".txt").toFile());
//                BufferedReader bufferedReader = new BufferedReader(reader);
//                String line = bufferedReader.readLine();
//                String[] parts = line.split(";");
//                modules.put(Integer.toUnsignedLong(i), new Module(Long.parseLong(parts[0]), parts[1], parts[2], modulePath));
//            }catch (Exception e) {
//
//                System.out.println("Error loading image: " + e.getMessage());
//            }
//        }
//    }
//
//    public void addModule(long courseId, String name, String description, MultipartFile content) {
//
//        Course course = courseList.get(courseId);
//        Module module = new Module(course.getNumberOfModules()+1, name, description, null);
//        saveCourseInDisk(courseId, course);
//    }
}