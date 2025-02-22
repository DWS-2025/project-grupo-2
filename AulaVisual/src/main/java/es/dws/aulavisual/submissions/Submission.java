package es.dws.aulavisual.submissions;

public class Submission {

    private final long id;
    private final long userId;
    private boolean graded;
    private float grade;

    public Submission(long id, long userId) {
        this.id = id;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    public float getGrade() {
        return grade;
    }
}