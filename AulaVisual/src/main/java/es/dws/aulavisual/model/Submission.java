package es.dws.aulavisual.model;

import java.sql.Blob;

import jakarta.persistence.*;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User student;

    @ManyToOne()
    private Course course;

    @Lob
    private Blob submission;

    private boolean graded = false;
    private float grade = -1;

    protected Submission() {


    }

    public Submission(Course course, User user, Blob submission) {
        this.course = course;
        this.student = user;
        this.submission = submission;
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

    public Blob getSubmission() {

        return submission;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public Course getCourse() {

        return this.course;
    }

    public User getStudent () {
        return this.student;
    }
}