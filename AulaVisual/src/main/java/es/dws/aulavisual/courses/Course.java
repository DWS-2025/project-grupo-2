package es.dws.aulavisual.courses;

import es.dws.aulavisual.submissions.Submission;
import es.dws.aulavisual.users.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private  String name;
    private  String description;
    private  String task;
    private  long teacherId;

    @ManyToMany()
    private  List <User> students;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "course")
    private  List <Module> modules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "course")
    private  List<Submission> submissions;

    protected Course() {

    }

    public Course(long id, String name, String description, long teacherId, List <Long> students, List<Module> modules, String task) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.teacherId = teacherId;
        this.students = students;
        this.modules = modules;
        this.submissions = new ArrayList<>();
        this.task = task;
    }

    public long getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public List <Module> getModules() {

        return modules;
    }

    public void addModule(Module module) {

        modules.add(module);
    }

    public void removeModule(Module module) {

        modules.remove(module);
    }

    public List <Long> getStudents(){

        return students;
    }

    public long getNumberModules() {

        return modules.size();
    }

    public Module getModuleById(long moduleId) {
        for (Module module : modules) {
            if (module.getId() == moduleId) {
                return module;
            }
        }
        return null;
    }

    public boolean userMadeSubmission(long userId) {
        for (Submission submission : submissions) {
            if (submission.getUser().getId() == userId) {
                return true;
            }
        }
        return false;
    }

    public void saveSubmission(User user) {

        Submission submission = new Submission(this.id, user);
        submissions.add(submission);
    }

    public void addSubmission(Submission submission) {

        submissions.add(submission);
    }

    public List<Submission> getSubmissions() {

        return submissions;
    }

    public String getTask() {

        return task;
    }

    public List <Submission> getUngradedSubmission() {
        List <Submission> ungraded = new ArrayList <>();
        for (Submission submission : submissions) {
            if (!submission.isGraded()) {
                ungraded.add(submission);
            }
        }
        return ungraded;
    }

    public List<Submission> getGradedSubmissions() {
        List <Submission> graded = new ArrayList <>();
        for (Submission submission : submissions) {
            if (submission.isGraded()) {
                graded.add(submission);
            }
        }
        return graded;
    }

    public long getTeacherId() {

        return teacherId;
    }

    public Submission getSubmission(long studentId) {

        for (Submission submission : submissions) {
            if (submission.getUser().getId() == studentId) {
                return submission;
            }
        }
        return null;
    }

    public void removeSubmission(Submission submission) {

        submissions.remove(submission);
    }

    public boolean addStudent(long studentId) {

        return students.add(studentId);
    }
}