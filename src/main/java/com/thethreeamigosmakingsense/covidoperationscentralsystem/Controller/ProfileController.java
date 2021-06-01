package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    /**
     * GetMapping for user profile page
     * @param model model
     * @return html
     */
    @GetMapping("/profile")
    private String profile(Model model) {

        model.addAttribute("navItem", "profile"); // active menu
        model.addAttribute("user", userService.fetchRemoteUser()); // user object

        return "profile/profile";
    }

    /**
     * PostMapping for changing user details
     * @param model model
     * @param user object
     * @return html
     */
    @PostMapping("/profile")
    private String profile(Model model, User user) {

        String returnString; // html
        if (userService.updateUser(user)) returnString = "profile/profile"; // if saved to database
        else returnString = "redirect:/profile?error";

        model.addAttribute("navItem", "profile"); // active menu
        model.addAttribute("user", userService.fetchRemoteUser()); // user object

        return returnString;
    }
}
