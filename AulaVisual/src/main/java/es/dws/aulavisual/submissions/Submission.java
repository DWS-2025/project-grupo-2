package es.dws.aulavisual.submissions;

import es.dws.aulavisual.courses.Course;
import es.dws.aulavisual.users.User;
import jakarta.persistence.*;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User student;

    @ManyToOne()
    private Course course;


    private boolean graded = false;
    private float grade = -1;

    protected Submission() {


    }

    public Submission(Course course, User user) {
        this.course = course;
        this.student = user;
    }

    public User getUser() {

        return student;
    }

    public void setGrade(float grade) {

        this.grade = grade;
        this.graded = true;
    }

    public boolean isGraded() {

        return graded;
    }

    public float getGrade() {

        return this.grade;
    }
}