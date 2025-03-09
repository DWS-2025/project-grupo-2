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
    private User userId;

    @ManyToOne()
    private Course course;


    private long courseId;
    private boolean graded = false;
    private float grade = -1;

    protected Submission() {


    }

    public Submission(long courseId, User user) {
        this.courseId = courseId;
        this.userId = user;
    }

    public long getcourseId() {
        return courseId;
    }

    public User getUser() {

        return userId;
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