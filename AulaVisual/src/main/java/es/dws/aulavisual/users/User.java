package es.dws.aulavisual.users;

import es.dws.aulavisual.courses.Course;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import es.dws.aulavisual.submissions.Submission;
import jakarta.persistence.*;

@Entity(name = "UserTable")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany(mappedBy = "students")
    private List<Course> courses = new ArrayList <>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "student")
    private List<Submission> submissions;

    @Lob
    private Blob image;

    private String realName;
    private String surname;
    private String userName;
    private String passwordHash;
    private int role; //0 for admin, 1 for teacher, 2 for student

    public User(String realName, String surname, String userName, String passwordHash, int role) {
        this.realName = realName;
        this.surname = surname;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.role = role;
        this.image = null;
    }

    protected User() {

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

    public Blob getImage() {

        return this.image;
    }

    public void setImage(Blob blob) {

        this.image = blob;
    }
}
