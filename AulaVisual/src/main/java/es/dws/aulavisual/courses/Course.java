package es.dws.aulavisual.courses;

import es.dws.aulavisual.users.User;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private long id;
    private String name;
    private String description;
    private List <Long> userIds;

    public Course(long id, String name, String description, long teacherId) {

        this.id = id;
        this.name = name;
        this.description = description;
        userIds = new ArrayList <>();
        userIds.add(teacherId);
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public long getTeacherId() {

        return userIds.get(0);
    }

    public List <Long> getUserIds() {

        return userIds;
    }
}