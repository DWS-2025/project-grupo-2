package es.dws.aulavisual.courses;

import java.util.List;
import java.util.Map;

public class Course {

    private final long id;
    private final String name;
    private final String description;
    private final long teacherId;
    private final List <Long> userIds;
    private final List <Module> modules;

    public Course(long id, String name, String description, long teacherId, List <Long> userIds, List<Module> modules) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.teacherId = teacherId;
        this.userIds = userIds;
        this.modules = modules;
    }

    public long getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public long getTeacherId() {

        return teacherId;
    }

    public List <Long> getUserIds() {

        return userIds;
    }

    public List <Module> getModules() {

        return modules;
    }

    public long getNumberOfModules() {

        return modules.size();
    }

    public void addModule(Module module) {

        modules.add(module);
    }

    public void removeModule(Module module) {

        modules.remove(module);
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
}