package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.DTO.UserCreationDTO;
import es.dws.aulavisual.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.service.UserService;

import java.net.URI;
import java.util.Collection;
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

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserCreationDTO userCreationDTO) {

        UserDTO userDTO = userService.updateDTO(id, userCreationDTO);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.id()).toUri();
        return ResponseEntity.created(location).body(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.findByIdDTO(id));
    }
}
