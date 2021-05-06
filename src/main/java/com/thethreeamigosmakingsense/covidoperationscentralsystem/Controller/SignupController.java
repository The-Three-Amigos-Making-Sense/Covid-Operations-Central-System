package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
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
    UserService userService;

    @GetMapping("/signup")
    String signUp(Model model) {

        model.addAttribute("navItem", "signup");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "signup/signup";
        }

        return "redirect:/";
    }

    @PostMapping("/signup")
    String signUp(User user) {

        Authority authority = new Authority(user, "ROLE_USER");
        if (userService.registerUser(user, authority)) {
            return "signup/success";
        }
        else {
            return "redirect:/signup?error";
        }
    }

    @GetMapping("/success")
    public String success() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "signup/success";
        }

        return "redirect:/";
    }
}
