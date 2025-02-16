package es.dws.aulavisual.courses;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseManager {

    private List <Course> courses;
    private long nextId;

    public CourseManager() {

        this.courses = new ArrayList <>();
        this.nextId = 1; // Initialising nextId to 1
    }

    public void addCourse(String name, String description, int duration, String image) {

        Course course = new Course(nextId++, name, description, duration, image);
        courses.add(course);
    }

    public List <Course> getCourses() {

        return courses;
    }

    public Course getCourse(long id) {

        for (Course course : courses) {
            if(course.getId() == id) {
                return course;
            }
        }
        return null;
    }

    public void removeCourse(long id) {

        courses.removeIf(course -> course.getId() == id);
    }
}