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

import es.dws.aulavisual.DTO.*;
import es.dws.aulavisual.Mapper.UserMapper;
import es.dws.aulavisual.model.Course;
import es.dws.aulavisual.model.User;
import es.dws.aulavisual.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    private User getLoggedUser(){

        return userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
    }

    public UserDTO saveDTO(String name, String surname, String userName, String password, String campus, String ...roles) {

        String passwordHash = passwordEncoder.encode(password);
        return userMapper.toDTO(save(name, surname, userName, passwordHash, campus, roles));
    }

    User save(String name, String surname, String userName, String passwordHash, String campus, String ...roles) {

        User user = new User(name, surname, userName, passwordHash, campus, roles);
        return userRepository.saveAndFlush(user);
    }

    public UserDTO saveDTO(UserCreationDTO userDTO) {

        User user = userMapper.toDomain(userDTO.userDTO());
        user.setPasswordHash(hashPassword(userDTO.password()));
        user.setUserName(userDTO.userDTO().userName());
        user.setRealName(userDTO.userDTO().realName());
        user.setSurname(userDTO.userDTO().surname());
        return userMapper.toDTO(save(user));
    }

    User save(User user) {

        return userRepository.saveAndFlush(user);
    }

    public UserDTO deleteById(long id) {

        User userToDelete = userRepository.findById(id).orElseThrow();
        List<Course> courses = userToDelete.getCourses();
        for (Course course : courses) {

            course.getStudents().remove(userToDelete);
        }

        if(userToDelete.getRole() == 1) {

            Course course = userToDelete.getCourseTeaching();
            course.setTeacher(null);
        }
        userRepository.deleteById(id);
        return userMapper.toDTO(userToDelete);
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

    public void saveImage(long userId, String location, InputStream inputStream, long size) {

        User user = userRepository.findById(userId).orElseThrow();

        user.setImage(location);
        user.setImageFile(BlobProxy.generateProxy(inputStream, size));
        userRepository.save(user);
    }

    public ResponseEntity <Object> loadImage(long requestedId) {

        User loggedUser = getLoggedUser();

        if(loggedUser.getId() != requestedId && !loggedUser.getRoles().contains("ADMIN")) {

            return ResponseEntity.status(403).body("Forbidden");
        }
        try {

            Blob image = loggedUser.getImageFile();
            if(image == null) {

                try {
                    ClassPathResource resource = new ClassPathResource("static/images/user-default.png");
                    byte [] imageBytes = resource.getInputStream().readAllBytes();
                    return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png").body(imageBytes);
                } catch (IOException e) {

                    return ResponseEntity.status(500).body("Internal Server Error");
                }
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

    void addCourseToTeacher(long id, Course course) {

        User user = userRepository.findById(id).orElseThrow();
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

    public Page<UserDTO> getAllUsers(Pageable pageable, Optional<String> campus, Optional<Integer> role, Optional<Boolean> removeSelf, Optional<Long> userId) {



        User exampleUser = new User();
        Example <User> example;
        if(role.isEmpty()){

            if(campus.isPresent() && !campus.get().isEmpty()) {

                exampleUser.setCampus(campus.get());
            }
            ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id", "role");
            example = Example.of(exampleUser, matcher);
        }else {

            exampleUser.setRole(role.get());
            if (campus.isPresent() && !campus.get().isEmpty()) {

                exampleUser.setCampus(campus.get());
            }
            ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id");
            example = Example.of(exampleUser, matcher);
        }

        Page<UserDTO> users = userRepository.findAll(example, pageable).map(userMapper::toDTO);
        if (removeSelf.isPresent() && removeSelf.get() && userId.isPresent()) {
            users = (Page<UserDTO>) users.filter(user -> !user.id().equals(userId.get()));
        }
        return users;
    }

    public UserDTO findByIdDTO(long id) {

        return userMapper.toDTO(findById(id));
    }

    User findById(long id) {

        return userRepository.findById(id).orElseThrow();
    }

    public UserDTO findByUserName(String userName) {

        return userMapper.toDTO(userRepository.findByUserName(userName).orElseThrow());
    }

    public UserDTO updateDTO(Long id, UserCreationDTO userCreationDTO) {

        User user = userRepository.findById(id).orElseThrow();
        user.setRealName(userCreationDTO.userDTO().realName());
        user.setSurname(userCreationDTO.userDTO().surname());
        user.setUserName(userCreationDTO.userDTO().userName());
        user.setCampus(userCreationDTO.userDTO().campus());
        return userMapper.toDTO(userRepository.save(user));
    }
}
