package es.dws.aulavisual.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private String content = null;

    private List<String> comments = new ArrayList <>();

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

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public void setSubmission(Blob blob) {

        this.submission = blob;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public List<String> getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object o) {

        if(o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(id);
    }
}