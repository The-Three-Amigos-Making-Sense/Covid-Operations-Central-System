package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * GetMapping for login page. Every url redirects here for unauthorized users (public pages excluded)
     * @param model model
     * @return html
     */
    @GetMapping("/login")
    private String showLoginForm(Model model) {

        model.addAttribute("navItem", "login");

        // If user is authenticated this page is not accessible
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login/login";
        }

        return "redirect:/";
    }
}
