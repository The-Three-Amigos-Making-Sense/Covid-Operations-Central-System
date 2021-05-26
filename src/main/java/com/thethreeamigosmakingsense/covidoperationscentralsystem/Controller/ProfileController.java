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

    @GetMapping("/profile")
    private String profile(Model model) {

        model.addAttribute("navItem", "profile");
        model.addAttribute("user", userService.fetchRemoteUser());

        return "profile/profile";
    }

    @PostMapping("/profile")
    private String profile(Model model, User user) {

        String returnString;
        if (userService.updateUser(user)) returnString = "profile/profile";
        else returnString = "redirect:/profile?error";


        model.addAttribute("navItem", "profile");
        model.addAttribute("user", userService.fetchRemoteUser());

        return returnString;
    }
}
