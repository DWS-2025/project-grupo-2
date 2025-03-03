package es.dws.aulavisual;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.users.UserManager;
import org.springframework.web.multipart.MultipartFile;
import es.dws.aulavisual.users.User;

@Controller
public class UserController {

    private final UserManager userManager;
    public UserController(UserManager userManager) {

        this.userManager = userManager;
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

            if(userManager.login(username, password)) {

                long userId = userManager.getUserId(username);
                // create a cookie
                Cookie cookie = new Cookie("userId", Long.toString(userId));
                cookie.setMaxAge(24 * 60 * 60); //1 day

                //add cookie to response
                response.addCookie(cookie);
                model.addAttribute("userName", username);
                if(userManager.getUser(userId).getRole() == 0) {

                    return "redirect:/admin";
                }else {

                    return "redirect:/courses";
                }
            }else {

                model.addAttribute("message", "Nombre de usuario o contraseña incorrectos");
                return "error";
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String password) {

        if(!(name.isEmpty() && surname.isEmpty() && username.isEmpty() && password.isEmpty())) {

            userManager.addUser(name, surname, username, password, 2);
            return "redirect:/login";
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

        User currentUser = userManager.getUser(Long.parseLong(userId));
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
            currentUser = userManager.getUser(Long.parseLong(userId));
            redirect = "/admin";
        }

        if(!username.equals(currentUser.getUserName())) {

            if(!userManager.updateUsername(Long.parseLong(userId), username)) {

                model.addAttribute("message", "Error al actualizar el usuario");
                return "error";
            }
        }

        redirect = "/logout";
        if(!newPassword.isEmpty()) {

            if(!userManager.updatePassword(Long.parseLong(userId), prevPassword, newPassword)) {

                model.addAttribute("message", "Error al actualizar la contraseña");
                return "error";
            }
        }

        if(image != null && !image.isEmpty()) {

            userManager.saveImage("user-" + Long.parseLong(userId), Long.parseLong(userId), image);
        }

        return "redirect:/profile/" + userId;
    }

    @GetMapping("/user_pfp/{id}")
    public ResponseEntity <Object> getUserPfp(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return ResponseEntity.status(401).body("Unauthorized");
        }
        User currentUser = userManager.getUser(Long.parseLong(userId));
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
        }
        return userManager.loadImage("user-" + Long.parseLong(userId), Long.parseLong(userId));

    }

    @GetMapping("/profile/{id}")
    public String getProfile(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User currentUser = userManager.getUser(Long.parseLong(userId));
        if(currentUser == null){

            model.addAttribute("message", "Usuario no encontrado");
            return "error";
        }
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
            currentUser = userManager.getUser(Long.parseLong(userId));
            model.addAttribute("id", userId);
        }
        model.addAttribute("userName", currentUser.getUserName());
        return "/users/userPage";

    }

    @GetMapping("/admin")
    public String getAdmin(Model model, @CookieValue(value = "userId", defaultValue = "") String userId) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            User currentUser = userManager.getUser(Long.parseLong(userId));
            if(currentUser.getRole() == 0) {

                model.addAttribute("admin", currentUser.getUserName());
                model.addAttribute("users", userManager.getAllUsers(currentUser));
                model.addAttribute("userId", Long.parseLong(userId));
                return "/users/adminPanel";
            }else {

                model.addAttribute("message", "No tienes permisos para acceder a esta página");
                return "error";
            }
        }
    }

    @GetMapping("/admin/users/{id}/delete")
    public String deleteUser(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            User currentUser = userManager.getUser(Long.parseLong(userId));
            if(currentUser.getRole() == 0) {

                if(userManager.removeUser(id)){

                    return "redirect:/admin";
                }else {

                    model.addAttribute("message", "Usuario no encontrado");
                    return "error";
                }

            }else {

                return "redirect:/";
            }
        }
    }

    @GetMapping("/admin/users/{id}")
    public String editUser(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            User currentUser = userManager.getUser(Long.parseLong(userId));
            if(currentUser.getRole() == 0) {

                User user = userManager.getUser(id);
                if(user == null) {

                    model.addAttribute("message", "Usuario no encontrado");
                    return "error";
                }
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

            User currentUser = userManager.getUser(Long.parseLong(userId));
            if(currentUser.getRole() == 0) {

                User user = userManager.getUser(id);
                if(user == null) {

                    model.addAttribute("message", "Usuario no encontrado");
                    return "error";
                }
                model.addAttribute("userId", Long.parseLong(userId));
                model.addAttribute("user", user.getRealName() + " " + user.getSurname());
                model.addAttribute("id", user.getId());
                return "/users/editRole";
            }else {

                return "redirect:/";
            }
        }
    }

    @GetMapping("/admin/users/{id}/roles/{role}")
    public String updateUserRole(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id, @PathVariable int role) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            User currentUser = userManager.getUser(Long.parseLong(userId));
            if(currentUser.getRole() == 0) {

                if(userManager.updateRole(id, role)){

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
