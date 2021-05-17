package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.UserRepository;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    private String profile(Model model) {

        User user = userRepository.fetchUser();

        model.addAttribute("navItem", "profile");
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("firstname", user.getFirstname());
        model.addAttribute("lastname", user.getLastname());
        model.addAttribute("phone_no", user.getPhone_no());

        return "profile/profile";
    }

    @PostMapping("/profile")
    public String profile(Model model, User user) {

        String returnString;
        if (userService.updateUser(user)) returnString = "profile/profile";
        else returnString = "redirect:/profile?error";

        user = userRepository.fetchUser();

        model.addAttribute("navItem", "profile");
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("firstname", user.getFirstname());
        model.addAttribute("lastname", user.getLastname());
        model.addAttribute("phone_no", user.getPhone_no());

        return returnString;



    }
}
