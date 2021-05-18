package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class searchController {

    @Autowired
    UserService userService;

    @GetMapping("/searchuser")
    public String search(Model model) {

        List<User> userList = userService.fetchAllUsersWithRole("ROLE_USER");
        for (User user : userList) {
            System.out.println(user.getUsername());
        }

        model.addAttribute("users", userService.fetchAllUsersWithRole("ROLE_USER"));

        return "search/search";
    }
}
