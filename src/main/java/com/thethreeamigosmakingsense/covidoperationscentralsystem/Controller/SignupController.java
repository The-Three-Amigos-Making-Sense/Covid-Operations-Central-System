package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    /**
     * GetMapping for account registration
     * @param model model
     * @return html
     */
    @GetMapping("/signup")
    private String signUp(Model model) {

        model.addAttribute("navItem", "signup"); // active menu

        // authenticated users are not allowed here
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "signup/signup";
        }

        return "redirect:/";
    }

    /**
     * PostMapping for account registration
     * @param user object
     * @return html
     */
    @PostMapping("/signup")
    private String signUp(User user) {

        if (userService.signupUser(user)) { // if user was saved to database
            return "signup/success";
        }
        else {
            return "redirect:/signup?error";
        }
    }

    /**
     * GetMapping for when account registration is a success
     * Authenticated users are prevented from accessing the page, and the page is not made public
     * in SecurityConfig. So it can only be accessed upon successful account registration.
     * @return html
     */
    @GetMapping("/success")
    private String success() {

        // Authenticated users are not allowed here
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "signup/success";
        }

        return "redirect:/";
    }
}
