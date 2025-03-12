package es.dws.aulavisual.users;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;

import es.dws.aulavisual.Paths;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public void save(String name, String surname, String userName, String password, int role) {

        String passwordHash = hashPassword(password);
        User user = new User(name, surname, userName, passwordHash, role);
        userRepository.save(user);
    }

    public Optional<User> findByUserName(String userName) {

        return userRepository.findByUserName(userName);
    }

    public Optional<User> findById(long id) {

        return userRepository.findById(id);
    }

    public void deleteById(long id) {

        userRepository.deleteById(id);
    }

    public void editUsername(long id, String newUsername) {

        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {

            System.out.println("User not found");
            return;
        }
        User userToEdit = user.get();
        userToEdit.setUserName(newUsername);
        userRepository.save(userToEdit);
    }

    public void editPassword(long id, String newPassword, String previousPassword) {

        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {

            System.out.println("User not found");
            return;
        }
        User userToEdit = user.get();
        if(!userToEdit.getPasswordHash().equals(hashPassword(previousPassword))) {

            System.out.println("Bad credentials");
            return;
        }
        userToEdit.setPasswordHash(hashPassword(newPassword));
        userRepository.save(userToEdit);
    }

    public String hashPassword(String password) {

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

    public boolean login(String userName, String password) {

        Optional<User> chechUser = userRepository.findByUserName(userName);
        if(chechUser.isEmpty()) {

            System.out.println("User " + userName + " not found");
            return false;
        }
        User user = chechUser.get();
        String passwordHash = hashPassword(password);
        if(user.getPasswordHash().equals(passwordHash)) {

            System.out.println("User " + user.getUserName() + " logged in");
            return true;
        }
        System.out.println("User " + user.getUserName() + " Bad Credentials");
        return false;
    }

    public void saveImage(User user, MultipartFile image) {

        try {
            user.setImage(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
            userRepository.save(user);
        }catch (IOException e) {

            throw new RuntimeException("Error processing image", e);
        }
    }

    public ResponseEntity <Object> loadImage(long userId) {

        Optional<User> searchUser = userRepository.findById(userId);
        if(searchUser.isEmpty()) {

            System.out.println("User not found");
            return null;
        }
        User user = searchUser.get();

        try {

            Blob image = user.getImage();
            if(image == null) {

                System.out.println("User has no image");
                return null;
            }else{

                Resource file = new InputStreamResource(image.getBinaryStream());
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png")
                        .contentLength(image.length()).body(file);
            }
        } catch (Exception e) {

            System.out.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    public List <User> getAllUsersExceptSelf(User currentUser) {

        List<User> users = userRepository.findAll();
        users.remove(currentUser);
        return users;
    }

    public boolean updateRole(User user, int newRole) {

        if(newRole >= 0 && newRole <= 2) {

            user.setRole(newRole);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
