package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest http;

    @GetMapping("/searchuser")
    public String search(Model model) {

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        model.addAttribute("navItem", "searchuser");
        model.addAttribute("users", userService.fetchAllUsersWithRole("ROLE_USER"));

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "search")
    public String search(Model model, String searchTerm) {

        model.addAttribute("searchTerm", searchTerm);

        if (searchTerm.isBlank())
            model.addAttribute("users", userService.fetchAllUsersWithRole("ROLE_USER"));
        else model.addAttribute("users", userService.searchAllUsers("ROLE_USER", searchTerm));

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "reset")
    public String reset(Model model) {

        return search(model);
    }
}
