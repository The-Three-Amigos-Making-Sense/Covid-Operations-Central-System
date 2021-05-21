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
    UserService userService;

    @Autowired
    HttpServletRequest http;

    @GetMapping("/searchuser")
    public String search(Model model) {

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        List<User> userList = userService.fetchAllUsers();
        filterUserList(userList);

        model.addAttribute("navItem", "searchuser");
        model.addAttribute("users", userList);

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "search")
    public String search(Model model, String searchTerm) {

        model.addAttribute("searchTerm", searchTerm);

        if (searchTerm.isBlank()) return search(model);
        else  {
            List<User> userList = userService.searchAllUsers(searchTerm);
            filterUserList(userList);
            model.addAttribute("users", userList);
        }

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "reset")
    public String reset(Model model) {

        return search(model);
    }

    /**
     * Method to filter the user list based on authority
     *
     * Users with ROLE_PERSONNEL will only be able to user with ROLE_USER
     * Users with ROLE_ADMIN is able to see every user
     * Removes ALL users from list if user with ROLE_USER bypasses the first check.
     *
     * @param userList is the list of users to filtered
     */
    private void filterUserList(List<User> userList) {

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER")) {
            userList.clear();

        } else if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_PERSONNEL")) {
            userList.removeIf(user ->
                    userService.checkPrivilege(user.getUsername()).equals("ROLE_PERSONNEL") ||
                            userService.checkPrivilege(user.getUsername()).equals("ROLE_ADMIN"));
        }
    }
}
