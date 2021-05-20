package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.TreeMap;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @GetMapping(value = "/user/{username}")
    public String readUserInfo(@PathVariable("username") String username, Model model) {

        model.addAttribute("user", userService.fetchUser(username));
        model.addAttribute("bookings", bookingService.fetchUsersBoookings(username));
        return "profile/user";
    }
}
