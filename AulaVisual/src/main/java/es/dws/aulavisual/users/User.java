package es.dws.aulavisual.users;

import es.dws.aulavisual.courses.Course;
import java.util.List;
import es.dws.aulavisual.submissions.Submission;
import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany(mappedBy = "userIds")
    private List<Course> courses;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userId")
    private List<Submission> submissions;


    private String realName;
    private String surname;
    private String userName;
    private String passwordHash;
    private int role; //0 for admin, 1 for teacher, 2 for student

    public User(String realName, String surname, String userName, String passwordHash, int role, long id) {
        this.realName = realName;
        this.surname = surname;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.role = role;
        this.id = id;
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
}
