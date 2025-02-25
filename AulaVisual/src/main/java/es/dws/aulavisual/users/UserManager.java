package es.dws.aulavisual.users;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.security.MessageDigest;

import es.dws.aulavisual.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserManager {

    private final Map <Long, User> userList = new HashMap <>();
    private long nextId;

    public UserManager() {

        loadNextId();
        loadUsersFromDisk();
    }

    public void addUser(String name, String surname, String userName, String password, int role) {

        long id = nextId;
        saveNextId();
        String passwordHash = hashPassword(password);
        User user = new User(name, surname, userName, passwordHash, role, id);
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
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
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
                User user = new User(parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]), nextId);
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

            if(user.getUserName().equals(username) && user.getPasswordHash().equals(passwordHash)) {

                System.out.println("User " + username + " logged in");
                return true;
            }
        }
        System.out.println("User " + username + " not found");
        return false;
    }

    public long getUserId(String username) {

        for (Map.Entry <Long, User> entry : userList.entrySet()) {

            if(entry.getValue().getUserName().equals(username)) {

                return entry.getKey();
            }
        }
        return -1;
    }

    public boolean removeUser(long userId) {

        if(userList.containsKey(userId)) {

            userList.remove(userId);
            try {

                Writer writer = new FileWriter(Paths.USERSMAPPATH);
                for (Map.Entry <Long, User> entry : userList.entrySet()) {

                    String line = entry.getKey() + ";" + entry.getValue().getRealName() + ";" + entry.getValue().getSurname() + ";" + entry.getValue().getUserName() + ";" + entry.getValue().getPasswordHash() + ";" + entry.getValue().getRole() + "\n";
                    writer.write(line);
                }
                writer.close();
                return true;
            }catch (IOException e) {

                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public boolean updateUsername(long iserId, String username) {

        User user = userList.get(iserId);
        if(removeUser(iserId)){

            user.setUserName(username);
            userList.put(iserId, user);
            saveUserInDisk(iserId, user);
            return true;
        }

        return false;
    }

    public boolean updatePassword(long userId, String prevPassword, String newPassword) {

        User user = userList.get(userId);
        if(removeUser(userId)){

            if(user.getPasswordHash().equals(hashPassword(prevPassword))) {

                user.setPasswordHash(hashPassword(newPassword));
                userList.put(userId, user);
                saveUserInDisk(userId, user);
                return true;
            }else {

                System.out.println("Password incorrect");
                userList.put(userId, user);
                saveUserInDisk(userId, user);
            }
        }
        return false;
    }

    public User getUser(long userId) {

        return userList.getOrDefault(userId, null);
    }

    public List<User> getAllUsers(User ...usersToExclude) {

        List <User> users = new ArrayList <>(List.copyOf(userList.values()));

        if(users.removeAll(List.of(usersToExclude))) {

            return users;
        }else{

            return null;
        }
    }

    public List <String> getAllIds() {

        List <String> ids = new ArrayList <>();
        for (Map.Entry <Long, User> entry : userList.entrySet()) {

            ids.add(entry.getKey().toString());
        }
        return ids;
    }

    public boolean updateRole(long id, int role) {

        User user = userList.get(id);
        if(user != null && role >= 0 && role <= 2) {

            if(removeUser(id)) {

                user.setRole(role);
                userList.put(id, user);
                saveUserInDisk(id, user);
                return true;
            }
        }

        return false;
    }

    public void saveImage(String folderName, long userId, MultipartFile image) {

        try {
            Path folder = Paths.USERIMGS.resolve(folderName);
            Files.createDirectories(folder); // Create the directory if it does not exist

            Path newImg = folder.resolve("image-" + userId + ".png");
            image.transferTo(newImg);
        } catch (Exception e) {

            System.out.println("Error saving image: " + e.getMessage());
        }
    }

    public ResponseEntity <Object> loadImage(String folderName, long userId) {

        try {
            Path folder = Paths.USERIMGS.resolve(folderName);
            Path imgPath = folder.resolve("image-" + userId + ".png");

            Resource img = new UrlResource(imgPath.toUri());

            if (!Files.exists(imgPath)) {

                folder = Paths.USERDEFAULTIMGFOLDER;
                imgPath = folder.resolve(Paths.USERDEFAULTIMGPATH);
                img = new UrlResource(imgPath.toUri());
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png").body(img);
        } catch (Exception e) {

            System.out.println("Error loading image: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
