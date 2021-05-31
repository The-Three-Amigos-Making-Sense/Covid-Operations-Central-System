package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.BookingType;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import groovy.lang.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HttpServletRequest http;

    @GetMapping("/searchuser")
    private String search(Model model) {

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        List<Tuple2<User, Authority>> userList = userService.fetchAllUsersAndAuthorities();

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_PERSONNEL"))
            userList.removeIf(user -> !user.getFirst().isEnabled());


        model.addAttribute("navItem", "searchuser");
        model.addAttribute("users", userList);

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "search")
    private String search(Model model, String searchTerm) {

        model.addAttribute("navItem", "searchuser");
        model.addAttribute("searchTerm", searchTerm);

        if (searchTerm.isBlank()) return search(model);
        else  {
            List<Tuple2<User, Authority>> userList = userService.searchAllUsers(searchTerm);
            model.addAttribute("users", userList);
        }

        return "search/search";
    }

    @PostMapping(value = "/searchuser", params = "reset")
    private String reset(Model model) {

        return search(model);
    }

    @GetMapping("/calendar")
    private String calendar(Model model) {

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        List<Tuple2<Booking, BookingType>> bookingList = bookingService.fetchAllBookings();
        bookingList.removeIf(bookingType ->
                bookingType.getSecond().getStatus().equals("CANCELLED"));
        bookingList.removeIf(booking ->
                booking.getFirst().getLocalDateTime().isBefore(LocalDateTime.now().plusMinutes(10)));

        model.addAttribute("navItem", "calendar");
        model.addAttribute("bookings", bookingList);

        return "search/calendar";
    }
}


