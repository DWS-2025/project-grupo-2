package es.dws.aulavisual.model;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionComment> comments = new ArrayList<>();

    private String submissionName;
    private boolean graded = false;
    private float grade = -1;


    public Submission(Course course, User user, String submissionName) {
        this.course = course;
        this.student = user;
        this.submissionName = submissionName;
    }
    protected Submission() {


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

    public String getSubmissionName() {

        return this.submissionName;
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

    public void addComment(SubmissionComment comment) {

        this.comments.add(comment);
    }

    public List<SubmissionComment> getComments() {

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