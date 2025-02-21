package es.dws.aulavisual.submissions;

public class Submission {

    private final long courseId;
    private final String submission;

    public Submission(long courseId, String submission) {
        this.courseId = courseId;
        this.submission = submission;
    }

    public long getCourseId() {
        return courseId;
    }

    public String getSubmission() {
        return submission;
    }
}