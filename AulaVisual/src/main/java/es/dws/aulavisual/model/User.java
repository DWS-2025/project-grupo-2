package es.dws.aulavisual.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity(name = "UserTable")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany(mappedBy = "students", fetch = FetchType.EAGER)
    private final List<Course> courses = new ArrayList <>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "student")
    private List<Submission> submissions;

    @OneToOne()
    private Course courseTeaching = null;

    private String image;

    @Lob
    private Blob imageFile;

    private String campus;
    private String realName;
    private String surname;
    private String userName;
    private String passwordHash;
    private int role; //0 for admin, 1 for teacher, 2 for student

    public User(String realName, String surname, String userName, String passwordHash, String campus, int role) {
        this.realName = realName;
        this.surname = surname;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.role = role;
        this.campus = campus;
        this.imageFile = null;
        this.image = null;
    }

    public User() {

    }

    public void setCourseTeaching(Course course) {
        this.courseTeaching = course;
    }

    public Course getCourseTeaching() {
        return courseTeaching;
    }

    public String getRealName() {

        return realName;
    }

    public String getSurname() {

        return surname;
    }

    public String getUserName() {

        return userName;
    }

    public String getPasswordHash() {

        return passwordHash;
    }

    public long getId() {

        return id;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public void setPasswordHash(String passwordHash) {

        this.passwordHash = passwordHash;
    }

    public int getRole() {

        return role;
    }

    public void setRole(int role) {

        this.role = role;
    }

    public String getImage() {

        return this.image;
    }

    public void setImage(String image) {

        this.image = image;
    }

    public Blob getImageFile() {

        return this.imageFile;
    }

    public void setImageFile(Blob blob) {

        this.imageFile = blob;
    }

    public List<Course> getCourses() {

        return courses;
    }

    public void setCampus(String campus) {

        this.campus = campus;
    }

    public String getCampus() {

        return campus;
    }

    public void setRealName(String realName) {

        this.realName = realName;
    }

    public void setSurname(String surname) {

        this.surname = surname;
    }
}
