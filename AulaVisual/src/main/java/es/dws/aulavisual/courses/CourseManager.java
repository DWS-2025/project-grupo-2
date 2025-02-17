package es.dws.aulavisual.courses;

import es.dws.aulavisual.Paths;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseManager {

    private final Map <Long, Course> courseList = new HashMap <>();
    private long nextId;

    public CourseManager() {

        loadNextId();
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

//    private void loadCourseFromDisk() {
//
//        try {
//
//            Reader reader = new FileReader(Paths.USERSMAPPATH);
//            BufferedReader bufferedReader = new BufferedReader(reader);
//            String line = bufferedReader.readLine();
//            while (line != null) {
//
//                String[] parts = line.split(";");
//                long nextId = Long.parseLong(parts[0]);
//                Course course = new User(parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]));
//                courseList.put(nextId, course);
//                line = bufferedReader.readLine();
//            }
//            reader.close();
//        }catch (IOException e) {
//
//            throw new RuntimeException(e);
//        }
//    }

    public void createCourse(long courseId, String name, String description, long teacherId) {

        long id = nextId;
        saveNextId();
        Course course = new Course(courseId, name, description, teacherId);
        saveCourseInDisk(id, course);
        courseList.put(id, course);
    }

    private void saveNextId() {

        try {

            this.nextId++;
            Writer writer = new FileWriter(Paths.CURRENTCOURSEIDPATH);
            String line = Long.toString(nextId);
            writer.write(line);
            writer.close();

        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private void saveCourseInDisk(long courseId, Course course) {

        try {

            Writer writer = new FileWriter(Paths.COURSESMAPPATH, true);
            String line = courseId + ";" + course.getName() + ";" + course.getDescription();
            writer.write(line);
            List <Long> userIds = course.getUserIds();
            for(long userId : userIds) {

                line = ";" + userId;
                writer.write(line);
            }
            writer.write("\n");
            writer.close();

        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}