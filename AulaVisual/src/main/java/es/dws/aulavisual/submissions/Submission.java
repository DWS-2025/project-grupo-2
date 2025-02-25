package es.dws.aulavisual.submissions;

import es.dws.aulavisual.users.User;

public class Submission {

    private final User user;
    private final long courseId;
    private boolean graded = false;
    private float grade = -1;

    public Submission(long courseId, User user) {
        this.courseId = courseId;
        this.user = user;
    }

    public long getcourseId() {
        return courseId;
    }

    public User getUser() {

        return user;
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