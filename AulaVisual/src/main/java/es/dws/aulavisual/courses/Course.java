package es.dws.aulavisual.courses;

import es.dws.aulavisual.users.User;
import java.util.ArrayList;
import java.util.List;

public class Course {

    private String name;
    private String description;
    private int duration;
    private String image;
    private List<User> users;

    public Course(String name, String description, int duration, String image) {

        this.name = name;
        this.description = description;
        this.duration = duration;
        this.image = image;
        this.users = new ArrayList<>();
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public int getDuration() {

        return duration;
    }

    public String getImage() {

        return image;
    }

    public List<User> getUsers() {

        return users;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setDuration(int duration) {

        this.duration = duration;
    }

    public void setImage(String image) {

        this.image = image;
    }

    public void addUser(User user) {

        this.users.add(user);
    }

    public void removeUser(User user) {

        this.users.remove(user);
    }
}