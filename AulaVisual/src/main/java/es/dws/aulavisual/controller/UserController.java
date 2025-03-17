package es.dws.aulavisual.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import es.dws.aulavisual.model.User;

import java.io.IOException;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "login";
        }else {

            return "redirect:/profile/" + userId;
        }
    }

    @PostMapping("/login")
    public String userLogin(Model model, @RequestParam String username, @RequestParam String password, HttpServletResponse response) {


        if(!(username.isEmpty() && password.isEmpty())) {

            if(userService.login(username, password)) {

                User currentUser = userService.findByUserName(username).get();
                long userId = currentUser.getId();
                // create a cookie
                Cookie cookie = new Cookie("userId", Long.toString(userId));
                cookie.setMaxAge(24 * 60 * 60); //1 day

                //add cookie to response
                response.addCookie(cookie);
                model.addAttribute("userName", username);
                if(currentUser.getRole() == 0) {

                    return "redirect:/admin";
                }else {

                    return "redirect:/courses";
                }
            }

            model.addAttribute("message", "Nombre de usuario o contraseña incorrectos");

        }else {

            model.addAttribute("message", "Todos los campos son obligatorios");
        }
        return "error";
    }

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String password, @RequestParam String campus) {

        if(!(name.isEmpty() && surname.isEmpty() && username.isEmpty() && password.isEmpty() && campus.isEmpty())) {

            if(!(campus.equals("Noxus") || campus.equals("Piltover") || campus.equals("Zaun"))) {

                model.addAttribute("message", "Campus inválido");
                return "error";
            }else{

                if(userService.findByUserName(username).isPresent()){

                    model.addAttribute("message", "El usuario ya existe");
                    return "error";
                }else {

                    userService.save(name, surname, username, password, campus, 2);
                    return "redirect:/login";
                }
            }
        }else{

            model.addAttribute("message", "Todos los campos son obligatorios");
        }

        return "redirect:/register";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("userId", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @PostMapping("/profile/update/{id}")
    public String updateUser(Model model, @PathVariable long id, @RequestParam String username, @RequestParam String prevPassword, @RequestParam String newPassword, MultipartFile image, @CookieValue(value = "userId", defaultValue = "") String userId) {

        String redirect = "/login";
        if(userId.isEmpty()) {

            return "redirect:" + redirect;
        }
        redirect = "/logout";

        Optional<User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "El usuario de la cookie no existe");
            return "error";
        }

        User currentUser = searchUser.get();
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
            Optional<User> searchUser2 = userService.findById(Long.parseLong(userId));
            if(searchUser2.isEmpty()) {

                model.addAttribute("message", "El usuario no existe");
                return "error";
            }
            currentUser = searchUser2.get();
            redirect = "/admin";
        }

        if(!username.equals(currentUser.getUserName())){

            userService.editUsername(currentUser.getId(), username);
        }

        if(!(newPassword.isEmpty() && prevPassword.isEmpty())) {

            userService.editPassword(currentUser.getId(), newPassword, prevPassword);
        }

        if(image != null && !image.isEmpty()) {

            userService.saveImage(currentUser, image);
        }

        return redirect;
    }

    @GetMapping("/user_pfp/{id}")
    public ResponseEntity <Object> getUserPfp(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return ResponseEntity.status(401).body("Unauthorized");
        }
        Optional<User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            return ResponseEntity.status(401).body("Unauthorized");
        }
        User currentUser = searchUser.get();
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
        }
        ResponseEntity <Object> image = userService.loadImage(Long.parseLong(userId));

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

    @GetMapping("/profile/{id}")
    public String getProfile(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        Optional<User> searchUser = userService.findById(Long.parseLong(userId));
        if(searchUser.isEmpty()) {

            model.addAttribute("message", "El usuario de la cookie no existe");
            return "error";
        }
        User currentUser = searchUser.get();
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
            searchUser = userService.findById(Long.parseLong(userId));
            if(searchUser.isEmpty()) {

                model.addAttribute("message", "El usuario no existe");
                return "error";
            }
            currentUser = searchUser.get();
            model.addAttribute("id", userId);
        }
        model.addAttribute("userName", currentUser.getUserName());
        return "/users/userPage";

    }

    @GetMapping("/admin")
    public String getAdmin(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @RequestParam(required = false) Optional<String> campus, @RequestParam(required = false) Optional<Integer> role) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            Optional <User> searchUser = userService.findById(Long.parseLong(userId));
            if(searchUser.isEmpty()) {

                model.addAttribute("message", "El usuario de la cookie no existe");
                return "error";
            }
            User currentUser = searchUser.get();
            if(currentUser.getRole() == 0) {

                User exampleUser = new User();
                Example <User> example;
                if(role.isEmpty()){

                    if(campus.isPresent() && !campus.get().isEmpty()) {

                        exampleUser.setCampus(campus.get());
                    }
                    ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id", "role");
                    example = Example.of(exampleUser, matcher);
                }else{

                    exampleUser.setRole(role.get());
                    if(campus.isPresent() && !campus.get().isEmpty()) {

                        exampleUser.setCampus(campus.get());
                    }
                    ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id");
                    example = Example.of(exampleUser, matcher);
                }
                model.addAttribute("admin", currentUser.getUserName());
                model.addAttribute("users", userService.getAllUsersExceptSelfFiltered(currentUser, example));
                model.addAttribute("userId", Long.parseLong(userId));
                return "/users/adminPanel";
            }else {

                model.addAttribute("message", "No tienes permisos para acceder a esta página");
                return "error";
            }
        }
    }

    @PostMapping("/admin/users/{id}/delete") //Cambiar HTLML a POST
    public String deleteUser(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            Optional<User> searchUser = userService.findById(Long.parseLong(userId));
            if(searchUser.isEmpty()) {

                model.addAttribute("message", "El usuario de la cookie no existe");
                return "error";
            }
            User currentUser = searchUser.get();
            if(currentUser.getRole() == 0) {

                userService.deleteById(id);
                return "redirect:/admin";

            }else{

                return "redirect:/";
            }
        }
    }

    @GetMapping("/admin/users/{id}")
    public String editUser(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            Optional <User> searchUser = userService.findById(Long.parseLong(userId));
            if(searchUser.isEmpty()) {

                model.addAttribute("message", "El usuario de la cookie no existe");
                return "error";
            }
            User currentUser = searchUser.get();
            if(currentUser.getRole() == 0) {

                searchUser = userService.findById(id);
                if(searchUser.isEmpty()) {

                    model.addAttribute("message", "Usuario no encontrado");
                    return "error";
                }
                User user = searchUser.get();
                model.addAttribute("user", user);
                model.addAttribute("userId", Long.parseLong(userId));
                return "redirect:/profile/" + id;
            }else {

                return "redirect:/";
            }
        }
    }

    @GetMapping("/admin/users/{id}/roles")
    public String editUserRole(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            Optional <User> searchUser = userService.findById(Long.parseLong(userId));
            if(searchUser.isEmpty()) {

                model.addAttribute("message", "El usuario de la cookie no existe");
                return "error";
            }
            User currentUser = searchUser.get();
            if(currentUser.getRole() == 0) {

                searchUser= userService.findById(id);
                if(searchUser.isEmpty()) {

                    model.addAttribute("message", "Usuario no encontrado");
                    return "error";
                }
                User user = searchUser.get();
                model.addAttribute("userId", Long.parseLong(userId));
                model.addAttribute("user", user.getRealName() + " " + user.getSurname());
                model.addAttribute("id", user.getId());
                return "/users/editRole";
            }else {

                return "redirect:/";
            }
        }
    }

    @GetMapping("/admin/users/{id}/roles/{role}") //Cambiar HTLML a POST
    public String updateUserRole(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id, @PathVariable int role) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            Optional <User> searchUser = userService.findById(Long.parseLong(userId));
            if(searchUser.isEmpty()) {

                model.addAttribute("message", "El usuario de la cookie no existe");
                return "error";
            }
            User currentUser = searchUser.get();
            if(currentUser.getRole() == 0) {

                Optional <User> searchUser2 = userService.findById(id);
                if(searchUser2.isEmpty()) {

                    model.addAttribute("message", "Usuario no encontrado");
                    return "error";
                }
                User user = searchUser2.get();
                if(userService.updateRole(user, role)){

                    return "redirect:/admin";
                }else {

                    model.addAttribute("message", "Usuario o Rol inválido");
                }
                return "redirect:/admin";
            }else {

                return "redirect:/";
            }
        }
    }
}
