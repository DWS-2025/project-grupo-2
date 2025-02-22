package es.dws.aulavisual.courses;

import es.dws.aulavisual.submissions.Submission;
import java.util.ArrayList;
import java.util.List;

public class Course {

    private final long id;
    private final String name;
    private final String description;
    private final String task;
    private final long teacherId;
    private final List <Long> userIds;
    private final List <Module> modules;
    private final List<Submission> submissions;

    public Course(long id, String name, String description, long teacherId, List <Long> userIds, List<Module> modules, String task) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.teacherId = teacherId;
        this.userIds = userIds;
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

    public List <Long> getUserIds(){
        return userIds;
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
            if (submission.getUserId() == userId) {
                return true;
            }
        }
        return false;
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
}