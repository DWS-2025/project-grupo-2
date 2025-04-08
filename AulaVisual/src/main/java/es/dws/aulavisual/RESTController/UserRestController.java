package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.DTO.UserCreationDTO;
import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final CourseService courseService;

    public UserRestController(UserService userService, CourseService courseService) {

        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/")
    public Page<UserDTO> users(Pageable pageable, @RequestParam(required = false) Optional<String> campus, @RequestParam(required = false) Optional<Integer> role, @RequestParam(required = false) Optional<Boolean> removeSelf, @RequestParam (required = false) Optional<Long> id) {

        return userService.getAllUsers(pageable, campus, role, removeSelf, id);
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreationDTO userCreationDTO) {

        UserDTO userDTO = userService.saveDTO(userCreationDTO);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.id()).toUri();

        return ResponseEntity.created(location).body(userDTO);
    }

    @PutMapping("/{id}/")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserCreationDTO userCreationDTO) {

        UserDTO userDTO = userService.updateDTO(id, userCreationDTO);
        courseService.updateRole(userDTO, userCreationDTO.userDTO().role());
        URI location = fromCurrentRequest().path("").buildAndExpand(userDTO.id()).toUri();
        return ResponseEntity.created(location).body(userService.findByIdDTO(userDTO.id()));
    }

    @GetMapping("/{id}/")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.findByIdDTO(id));
    }

    @PutMapping("/{id}/image/")
    public ResponseEntity<Object> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {

        try {
            String location = fromCurrentRequest().path("").buildAndExpand(id).toUri().getPath();
            userService.saveImage(id, location, image.getInputStream(), image.getSize());
            return ResponseEntity.created(URI.create(location)).body(location);
        }catch (IOException e){

            return ResponseEntity.badRequest().body("Error uploading image: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/image/")
    public ResponseEntity<Object> loadImage(@PathVariable Long id) {

        UserDTO userDTO = userService.findByIdDTO(id);
        ResponseEntity<Object> image = userService.loadImage(userDTO);
        if(image == null) {

            try {
                ClassPathResource resource = new ClassPathResource("static/images/user-default.png");
                byte [] imageBytes = resource.getInputStream().readAllBytes();
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/png").body(imageBytes);
            } catch (IOException e) {

                return ResponseEntity.status(500).body("Internal Server Error");
            }
        }else {

            return image;
        }
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {

        UserDTO deletedUser = userService.deleteById(id);
        return ResponseEntity.ok().body(deletedUser);
    }
}
