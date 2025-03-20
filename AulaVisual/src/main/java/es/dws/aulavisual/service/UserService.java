package es.dws.aulavisual.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.Mapper.UserMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.repository.CourseRepository;
import es.dws.aulavisual.model.User;
import es.dws.aulavisual.repository.UserRepository;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CourseRepository courseService;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, CourseRepository courseService, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.courseService = courseService;
        this.userMapper = userMapper;
    }

    public void save(String name, String surname, String userName, String password, String campus, int role) {

        String passwordHash = hashPassword(password);
        User user = new User(name, surname, userName, passwordHash, campus, role);
        userRepository.save(user);
    }

    public void save(User user) {

        userRepository.save(user);
    }

    public void deleteById(long id) {

        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {

            System.out.println("User not found");
            return;
        }
        User userToDelete = user.get();
        List<Course> courses = userToDelete.getCourses();
        for (Course course : courses) {

            course.getStudents().remove(userToDelete);
            courseService.save(course);
        }

        if(userToDelete.getRole() == 1) {

            Course course = userToDelete.getCourseTeaching();
            course.setTeacher(null);
            courseService.save(course);
        }
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

    public void saveImage(UserDTO userDTO, URI location, InputStream inputStream, long size) {

        User user = userMapper.toDomain(userDTO);

        user.setImage(location.toString());
        user.setImageFile(BlobProxy.generateProxy(inputStream, size));
        userRepository.save(user);
    }

    public ResponseEntity <Object> loadImage(long userId) {

        Optional<User> searchUser = userRepository.findById(userId);
        if(searchUser.isEmpty()) {

            System.out.println("User not found");
            return null;
        }
        User user = searchUser.get();

        try {

            Blob image = user.getImageFile();
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

    public List <UserDTO> getAllUsersExceptSelfFiltered(UserDTO currentUserDTO, Example <User> example) {

        User currentUser = userMapper.toDomain(currentUserDTO);
        List<User> users = userRepository.findAll(example);
        users.remove(currentUser);
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public boolean updateRole(UserDTO userDTO, int newRole) {

        User user = userMapper.toDomain(userDTO);
        if(newRole >= 0 && newRole <= 2) {

            user.setRole(newRole);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void removeAllUsersFromCourse(Course course) {

        List<User> users = userRepository.findAll();

        for (User user : users) {

            user.getCourses().remove(course);
            userRepository.save(user);
        }
    }

    public void addCourseToTeacher(UserDTO userDTO, Course course) {

        User user = userMapper.toDomain(userDTO);
        user.setCourseTeaching(course);
        userRepository.save(user);
    }

    public List<UserDTO> getAvaliableTeachers() {

        return userRepository.findAllByRoleAndCourseTeachingNull(1).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
///////////////////////////////////////////////////////////////////////////////////
    public List<UserDTO> getAllUsers() {

        return userMapper.toDTOs(userRepository.findAll());
    }

    public UserDTO findById(long id) {

        return userMapper.toDTO(userRepository.findById(id).orElseThrow());
    }

    public UserDTO findByUserName(String userName) {

        return userMapper.toDTO(userRepository.findByUserName(userName).orElseThrow());
    }
}
