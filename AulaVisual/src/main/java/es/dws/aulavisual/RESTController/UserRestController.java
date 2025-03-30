package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.DTO.UserCreationDTO;
import es.dws.aulavisual.DTO.UserDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> users() {

        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
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
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.id()).toUri();
        return ResponseEntity.created(location).body(userDTO);
    }

    @GetMapping("/{id}/")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.findByIdDTO(id));
    }

    @PostMapping("/{id}/image/")
    public ResponseEntity<Object> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {

        try {
            URI location = fromCurrentRequest().path("/{id}/image/").buildAndExpand(id).toUri();
            userService.saveImage(id, location, image.getInputStream(), image.getSize());
            return ResponseEntity.created(location).body(location);
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
