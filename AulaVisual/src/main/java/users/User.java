package users;

public class User {

    private final String realName;
    private final String surname;
    private String userName;
    private String passwordHash;
    private int role; //0 for admin, 1 for teacher, 2 for student

    public User(String realName, String surname, String userName, String passwordHash, int role) {
        this.realName = realName;
        this.surname = surname;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.role = role;
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
