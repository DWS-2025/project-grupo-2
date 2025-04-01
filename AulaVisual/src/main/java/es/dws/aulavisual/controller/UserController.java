package es.dws.aulavisual.controller;

import es.dws.aulavisual.DTO.UserDTO;
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
import java.net.URI;
import java.util.NoSuchElementException;
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


        try {
            if(!(username.isEmpty() && password.isEmpty())) {

                if(userService.login(username, password)) {

                    UserDTO currentUser = userService.findByUserName(username);
                    long userId = currentUser.id();
                    // create a cookie
                    Cookie cookie = new Cookie("userId", Long.toString(userId));
                    cookie.setMaxAge(24 * 60 * 60); //1 day

                    //add cookie to response
                    response.addCookie(cookie);
                    model.addAttribute("userName", username);
                    if(currentUser.role() == 0) {

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
        }catch (NoSuchElementException e){

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String password, @RequestParam String campus) {

        try {
            if(!(name.isEmpty() && surname.isEmpty() && username.isEmpty() && password.isEmpty() && campus.isEmpty())) {

                if(!(campus.equals("Noxus") || campus.equals("Piltover") || campus.equals("Zaun"))) {

                    model.addAttribute("message", "Campus inválido");
                    return "error";
                }else{

                    UserDTO user = userService.findByUserName(username);
                    model.addAttribute("message", "Nombre de usuario ya en uso");
                    return "error";
                }
            }else{

                model.addAttribute("message", "Todos los campos son obligatorios");
            }

            return "redirect:/register";
        }catch (NoSuchElementException e) {

            userService.saveDTO(name, surname, username, password, campus, 2);
            return "redirect:/login";
        }
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

        try {
            String redirect = "/login";
            if(userId.isEmpty()) {

                return "redirect:" + redirect;
            }
            redirect = "/logout";

            UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
            if(currentUser.role() == 0 && id != Long.parseLong(userId)) {

                userId = Long.toString(id);
                currentUser = userService.findByIdDTO(Long.parseLong(userId));
                redirect = "/admin";
            }

            if(!username.equals(currentUser.userName())){

                userService.editUsername(currentUser.id(), username);
            }

            if(!(newPassword.isEmpty() && prevPassword.isEmpty())) {

                userService.editPassword(currentUser.id(), newPassword, prevPassword);
            }

            if(image != null && !image.isEmpty()) {

                URI location = new URI("/api/user/" + currentUser.id() + "/image");
                userService.saveImage(currentUser.id(), location.toString(), image.getInputStream(), image.getSize());
            }

            return redirect;
        }catch (NoSuchElementException | IOException  | java.net.URISyntaxException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/user_pfp/{id}")
    public ResponseEntity <Object> getUserPfp(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        try {
            if(userId.isEmpty()) {

                return ResponseEntity.status(401).body("Unauthorized");
            }
            UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
            if(currentUser.role() == 0 && id != Long.parseLong(userId)) {

                userId = Long.toString(id);
            }
            return userService.loadImage(currentUser);
        }catch (NoSuchElementException e) {

            return ResponseEntity.status(404).body("Not Found");
        }
    }

    @GetMapping("/profile/{id}")
    public String getProfile(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }
            UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
            if(currentUser.role() == 0 && id != Long.parseLong(userId)) {

                userId = Long.toString(id);
                currentUser = userService.findByIdDTO(Long.parseLong(userId));
                model.addAttribute("id", userId);
            }
            model.addAttribute("userName", currentUser.userName());
            return "/users/userPage";
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }

    }

    @GetMapping("/admin")
    public String getAdmin(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @RequestParam(required = false) Optional<String> campus, @RequestParam(required = false) Optional<Integer> role) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }else {

                UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
                if(currentUser.role() == 0) {
                    model.addAttribute("admin", currentUser.userName());
                    model.addAttribute("users", userService.getAllUsers());
                    model.addAttribute("userId", Long.parseLong(userId));
                    return "/users/adminPanel";
                }else {

                    model.addAttribute("message", "No tienes permisos para acceder a esta página");
                    return "error";
                }
            }
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/users/{id}/delete") //Cambiar HTLML a POST
    public String deleteUser(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }else {

                UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
                if(currentUser.role() == 0) {

                    userService.deleteById(id);
                    return "redirect:/admin";

                }else{

                    return "redirect:/";
                }
            }
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/users/{id}")
    public String editUser(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }else {

                UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
                if(currentUser.role() == 0) {

                    UserDTO user = userService.findByIdDTO(id);
                    model.addAttribute("user", user);
                    model.addAttribute("userId", Long.parseLong(userId));
                    return "redirect:/profile/" + id;
                }else {

                    return "redirect:/";
                }
            }
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/users/{id}/roles")
    public String editUserRole(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }else {

                UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
                if(currentUser.role() == 0) {

                    UserDTO user = userService.findByIdDTO(id);
                    model.addAttribute("userId", Long.parseLong(userId));
                    model.addAttribute("user", user.realName() + " " + user.surname());
                    model.addAttribute("id", user.id());
                    return "/users/editRole";
                }else {

                    return "redirect:/";
                }
            }
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/users/{id}/roles/{role}") //Cambiar HTLML a POST
    public String updateUserRole(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id, @PathVariable int role) {

        try {
            if(userId.isEmpty()) {

                return "redirect:/login";
            }else {

                UserDTO currentUser = userService.findByIdDTO(Long.parseLong(userId));
                if(currentUser.role() == 0) {

                    UserDTO user = userService.findByIdDTO(id);
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
        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
}
