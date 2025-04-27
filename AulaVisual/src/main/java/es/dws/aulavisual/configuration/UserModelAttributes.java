package es.dws.aulavisual.configuration;

import es.dws.aulavisual.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

@ControllerAdvice(basePackages = "es.dws.aulavisual.controller")
public class UserModelAttributes {

    /**
     * Adds the user attribute to the model for all controllers in the specified package.
     *
     * @param model   the model to add attributes to
     * @param request the HTTP request
     */

    private final UserService userService;

    public UserModelAttributes(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        if(principal != null) {
            model.addAttribute("user", userService.findByUserName(principal.getName()));
        }else {
            model.addAttribute("user", null);
        }
    }

}
