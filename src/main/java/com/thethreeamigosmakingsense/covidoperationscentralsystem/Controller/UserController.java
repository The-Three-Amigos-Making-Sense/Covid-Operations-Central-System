package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    HttpServletRequest http;

    @GetMapping(value = "/user/{username}")
    public String readUserInfo(@PathVariable("username") String username, Model model) {

        User user = userService.fetchUser(username);
        List<Tuple2<Booking, BookingType>> bookingList = bookingService.fetchUsersBoookings(username);
        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList);

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        model.addAttribute("hasTestBooking", hasBookings.getFirst());
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond());
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingList);

        return "profile/user";
    }

    @GetMapping("/mybookings")
    private String myBookings(Model model){

        List<Tuple2<Booking, BookingType>> bookingList = bookingService.fetchUsersBoookings(http.getRemoteUser());
        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList);

        model.addAttribute("navItem", "mybookings");
        model.addAttribute("hasTestBooking", hasBookings.getFirst());
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond());
        model.addAttribute("bookings", bookingList);

        model.addAttribute("bookings", bookingService.fetchUsersBoookings(http.getRemoteUser()));
        return "profile/myBookings";
    }

    private Tuple2<Boolean, Boolean> userHasBooking(List<Tuple2<Booking, BookingType>> bookingList) {

        boolean hasTestBooking = false;
        boolean hasVaccineBooking = false;

        for (Tuple2<Booking, BookingType> booking : bookingList ) {
            if (!hasTestBooking)
                if (booking.getFirst().getType().equals("TEST")) hasTestBooking = true;

            if (!hasVaccineBooking)
                if (booking.getFirst().getType().equals("VACCINE")) hasVaccineBooking = true;

            if (hasTestBooking && hasVaccineBooking)
                break;
        }

        return new Tuple2<>(hasTestBooking, hasVaccineBooking);
    }
}
