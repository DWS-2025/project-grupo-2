package Users;

public class User {
    private String name;
    private String email;
    private String password;
    private boolean isAdmin;

    public User(String name, String email, String password, boolean role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUserAdmin() {
        return isAdmin;
    }

}
