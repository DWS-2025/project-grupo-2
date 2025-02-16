package es.dws.aulavisual.courses;

import es.dws.aulavisual.Paths;
import es.dws.aulavisual.users.User;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
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

    public void createCourse(String name, String description, int duration, String image) {


    }
}