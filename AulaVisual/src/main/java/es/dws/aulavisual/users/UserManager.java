package es.dws.aulavisual.users;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;

import es.dws.aulavisual.Paths;
import org.springframework.stereotype.Service;

@Service
public class UserManager {

    private final Map <Long, User> userList = new HashMap <>();
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
        userList.put(id, user);
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
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private void saveUserInDisk(long id, User user) {

        try {

            Writer writer = new FileWriter(Paths.USERSMAPPATH, true);
            String line = id + ";" + user.getRealName() + ";" + user.getSurname() + ";" + user.getUserName() + ";" + user.getPasswordHash() + ";" + user.getRole() + "\n";
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

    public boolean login(String username, String password) {

        String passwordHash = hashPassword(password);
        for (User user : userList.values()) {

            if (user.getUserName().equals(username) && user.getPasswordHash().equals(passwordHash)) {

                System.out.println("User " + username + " logged in");
                return true;
            }
        }
        System.out.println("User " + username + " not found");
        return false;
    }
}
