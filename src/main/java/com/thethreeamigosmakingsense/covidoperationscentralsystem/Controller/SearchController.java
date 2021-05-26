package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest http;

    @GetMapping("/searchuser")
    private String search(Model model) {

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        model.addAttribute("navItem", "searchuser");
        model.addAttribute("users", userService.fetchAllUsersAndAuthorities());

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "search")
    private String search(Model model, String searchTerm) {

        model.addAttribute("navItem", "searchuser");
        model.addAttribute("searchTerm", searchTerm);

        if (searchTerm.isBlank()) return search(model);
        else  {
            List<User> userList = userService.searchAllUsers(searchTerm);
            model.addAttribute("users", userList);
        }

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "reset")
    private String reset(Model model) {

        return search(model);
    }
}
