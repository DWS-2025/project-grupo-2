package users;

import org.springframework.stereotype.Component;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import es.dws.aulavisual.Paths;
import java.security.MessageDigest;

@Component
public class UserManager {

    private final Map <Long,User> userList = new HashMap <>();
    private long nextId;

    public UserManager(){

        loadNextId();
        loadUsersFromDisk();
    }

    public void addUser(String name, String surname, String userName, String password, int role) {

        long id = nextId;
        saveNextId();
        String passwordHash = hashPassword(password);
        User user = new User(name, surname, userName, passwordHash, role);
        saveUserInDisk(id, user);
    }

    private void loadNextId() {
        try {

            Reader reader = new FileReader(Paths.CURRENTUSERIDPATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            nextId = Long.parseLong(line);
            reader.close();
        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private void saveNextId() {

        try {

            this.nextId++;
            Writer writer = new FileWriter(Paths.CURRENTUSERIDPATH);
            String line = Long.toString(nextId);
            writer.write(line);
            writer.close();

        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private String hashPassword(String password) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            return new String(messageDigest.digest());
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserInDisk(long id, User user) {

        try {

            Writer writer = new FileWriter(Paths.USERSMAPPATH, true);
            String line = id + ";" + user.getRealName() + ";" + user.getSurname() + ";" + user.getUserName() + ";" + user.getPasswordHash() + ";" + Integer.toString(user.getRole());
            writer.write(line);
            writer.close();

        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    private void loadUsersFromDisk() {

        try {

            Reader reader = new FileReader(Paths.USERSMAPPATH);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while (line != null) {

                String[] parts = line.split(";");
                long nextId = Long.parseLong(parts[0]);
                User user = new User(parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]));
                userList.put(nextId, user);
                line = bufferedReader.readLine();
            }
            reader.close();
        }catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
