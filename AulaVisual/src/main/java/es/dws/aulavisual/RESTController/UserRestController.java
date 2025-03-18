package es.dws.aulavisual.RESTController;

import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import es.dws.aulavisual.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public Collection<UserDTO> users() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {

        return userService.findbyId(id);
    }
}
