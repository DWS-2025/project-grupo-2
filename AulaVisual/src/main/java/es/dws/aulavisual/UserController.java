package es.dws.aulavisual;

import es.dws.aulavisual.Images.ImageManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.dws.aulavisual.users.UserManager;
import org.springframework.web.multipart.MultipartFile;
import es.dws.aulavisual.users.User;

import java.util.List;

@Controller
public class UserController {

    private final UserManager userManager;
    private final ImageManager imageManager;

    public UserController(UserManager userManager, ImageManager imageManager) {

        this.userManager = userManager;
        this.imageManager = imageManager;
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


        if(userManager.login(username, password)) {

            long userId = userManager.getUserId(username);
            // create a cookie
            Cookie cookie = new Cookie("userId", Long.toString(userId));
            cookie.setMaxAge(24 * 60 * 60); //1 day

            //add cookie to response
            response.addCookie(cookie);
            model.addAttribute("userName", username);
            return "welcome";
        }else {

            return "redirect:/login/error";
        }
    }

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String surname, @RequestParam String username, @RequestParam String password) {

        userManager.addUser(name, surname, username, password, 2);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("userId", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @PostMapping("/profile/update/{id}")
    public String updateUser(@PathVariable long id, @RequestParam String username, @RequestParam String prevPassword, @RequestParam String newPassword, MultipartFile image, @CookieValue(value = "userId", defaultValue = "") String userId) {

        String redirect = "/login";
        if(userId.isEmpty()) {

            return "redirect:" + redirect;
        }

        redirect = "/login";
        User currentUser = userManager.getUser(Long.parseLong(userId));
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
            redirect = "/admin";
        }

        if(!username.isEmpty()) {

            if(userManager.updateUsername(Long.parseLong(userId), username)) {

                return "redirect:" + redirect;
            }else {

                return "redirect:/profile/error";
            }
        }

        redirect = "/logout";
        if(!newPassword.isEmpty()) {

            if(userManager.updatePassword(Long.parseLong(userId), prevPassword, newPassword)) {

                return "redirect:" + redirect;
            }else {

                return "redirect:/profile/error";
            }
        }

        if(image != null && !image.isEmpty()) {

            imageManager.saveImage("user-" + Long.parseLong(userId), Long.parseLong(userId), image);
        }

        return "redirect:/profile";
    }

    @GetMapping("/user_pfp/{id}")
    public ResponseEntity <Object> getUserPfp(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return ResponseEntity.notFound().build();
        }
        User currentUser = userManager.getUser(Long.parseLong(userId));
        if(currentUser.getRole() == 0 && id != Long.parseLong(userId)) {

            userId = Long.toString(id);
            currentUser = userManager.getUser(Long.parseLong(userId));
        }
        return imageManager.loadImage("user-" + Long.parseLong(userId), Long.parseLong(userId));

    }

    @GetMapping("/profile/{id}")
    public String getProfile(Model model, @CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }
        User currentUser = userManager.getUser(Long.parseLong(userId));
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

                return "redirect:/";
            }
        }
    }

    @GetMapping("/admin/users/{id}/delete")
    public String deleteUser(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            User currentUser = userManager.getUser(Long.parseLong(userId));
            if(currentUser.getRole() == 0) {

                userManager.removeUser(id);
                return "redirect:/admin";
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
    public String updateUserRole(@CookieValue(value = "userId", defaultValue = "") String userId, @PathVariable long id, @PathVariable int role) {

        if(userId.isEmpty()) {

            return "redirect:/login";
        }else {

            User currentUser = userManager.getUser(Long.parseLong(userId));
            if(currentUser.getRole() == 0) {

                userManager.updateRole(id, role);
                return "redirect:/admin";
            }else {

                return "redirect:/";
            }
        }
    }
}
