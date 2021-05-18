package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class searchController {

    @Autowired
    UserService userService;

    @GetMapping("/searchuser")
    public String search(Model model) {

        model.addAttribute("navItem", "searchuser");
        model.addAttribute("users", userService.fetchAllUsersWithRole("ROLE_USER"));

        return "search/search";
    }

    @PostMapping("/searchuser")
    public String search(Model model, String searchTerm) {

        model.addAttribute("searchTerm", searchTerm);

        if (searchTerm.isBlank()) model.addAttribute("users", userService.fetchAllUsersWithRole("ROLE_USER"));
        else model.addAttribute("users", userService.searchAllUsers("ROLE_USER", searchTerm));

        return "search/search";
    }
}
