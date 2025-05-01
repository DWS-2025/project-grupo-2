package es.dws.aulavisual.controller;

import es.dws.aulavisual.DTO.UserDTO;
import es.dws.aulavisual.service.CourseService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final CourseService courseService;

    public UserController(UserService userService, CourseService courseService) {

        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/login")
    public String login(Model model) {

        if (model.getAttribute("user") != null) {

            return "redirect:/login/success";
        }else {

            return "login";
        }
    }

    @GetMapping("/login/success")
    public String succes(Model model) {

        UserDTO currentUser = (UserDTO) model.getAttribute("user");

        if(currentUser != null) {

            if(currentUser.role().equals("ADMIN")) {

            return "redirect:/admin";
            }else if(currentUser.role().equals("TEACHER"))

                return "redirect:/teacher/courses/" + currentUser.courseTeaching().id() + "/submissions";
            else {

                return "redirect:/courses";
            }
        }else{

            return "redirect:/login";
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
                }else{

                    UserDTO user = userService.findByUserName(username);
                    model.addAttribute("message", "Nombre de usuario ya en uso");
                }
                return "error";
            }else{

                model.addAttribute("message", "Todos los campos son obligatorios");
            }

            return "redirect:/register";
        }catch (NoSuchElementException e) {

            userService.saveDTO(name, surname, username, password, campus, "USER");
            return "redirect:/login";
        }
    }

    @PostMapping("/profile/update/{id}")
    public String updateUser(Model model, @PathVariable long id, @RequestParam String username, @RequestParam String prevPassword, @RequestParam String newPassword, MultipartFile image) {


        try{
            boolean logout = true;
            UserDTO currentUser = (UserDTO) model.getAttribute("user");
            if(currentUser.role().equals("ADMIN") && id != currentUser.id()) {

                currentUser = userService.findByIdDTO(id);
                logout = false;
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

            if(logout){

                return "logout";
            }else{

                return "redirect:/admin";
            }
        }catch (URISyntaxException | IOException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/user_pfp/{id}")
    public ResponseEntity <Object> getUserPfp(@PathVariable long id) {

        return userService.loadImage(id);

    }

    @GetMapping("/profile/{id}")
    public String getProfile(Model model, @PathVariable long id) {


            UserDTO currentUser = (UserDTO) model.getAttribute("user");
            if(currentUser.role().contains("ADMIN") && id != currentUser.id()) {

               currentUser = userService.findByIdDTO(id);
            }
            model.addAttribute("user", currentUser);
            return "/users/userPage";
    }

    @GetMapping("/admin")
    public String getAdmin(Model model) {

       return "/users/adminPanel";
    }

    @GetMapping("/admin/users/{id}/delete")
    public String deleteUserPage(Model model, @PathVariable long id) {

        try {
            UserDTO student = userService.findByIdDTO(id);
            model.addAttribute("student", student);
            return "/users/deleteUser";
        }catch (NoSuchElementException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(Model model, @PathVariable long id) {

        try {

            userService.deleteById(id);
            return "redirect:/admin";

        }catch (RuntimeException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/users/{id}/roles")
    public String editUserRole(Model model, @PathVariable long id) {

        try {

            UserDTO user = userService.findByIdDTO(id);
            model.addAttribute("student", user.realName() + " " + user.surname());
            model.addAttribute("id", user.id());
            return "/users/editRole";

        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/admin/users/{id}/roles/{role}")
    public String updateUserRole(Model model, @PathVariable int role, @PathVariable long id) {

        try {

                UserDTO user = userService.findByIdDTO(id);
                if(courseService.updateRole(user, role)){

                    return "redirect:/admin";
                }else {

                    model.addAttribute("message", "Usuario o Rol inválido");
                    return "error";
                }

        }catch (NoSuchElementException e) {

            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
}
