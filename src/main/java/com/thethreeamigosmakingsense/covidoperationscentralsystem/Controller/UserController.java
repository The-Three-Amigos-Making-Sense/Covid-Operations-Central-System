package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.*;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import groovy.lang.Tuple2;
import groovy.lang.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        List<Tuple3<Booking, BookingType, Boolean>> bookingList = bookingService.fetchUsersBoookings(username);
        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList);

        boolean canCancelTest = canCancel(bookingList, "TEST");
        boolean canCancelVaccine = canCancel(bookingList, "VACCINE");

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        model.addAttribute("hasTestBooking", hasBookings.getFirst());
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond());
        model.addAttribute("canCancelTest", canCancelTest);
        model.addAttribute("canCancelVaccine", canCancelVaccine);
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingList);

        return "profile/user";
    }

    @GetMapping("/mybookings")
    private String myBookings(Model model){

        List<Tuple3<Booking, BookingType, Boolean>> bookingList =
                bookingService.fetchUsersBoookings(http.getRemoteUser());

        filterBookings(bookingList);

        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList);

        boolean canCancelTest = canCancel(bookingList, "TEST");
        boolean canCancelVaccine = canCancel(bookingList, "VACCINE");

        model.addAttribute("navItem", "mybookings");
        model.addAttribute("validate", true);
        model.addAttribute("hasTestBooking", hasBookings.getFirst());
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond());
        model.addAttribute("canCancelTest", canCancelTest);
        model.addAttribute("canCancelVaccine", canCancelVaccine);
        model.addAttribute("bookings", bookingList);
        return "profile/myBookings";
    }

    @PostMapping(value = "/mybookings", params = "cancelTest")
    private String myBookings(Model model, String id, String status) {

        TestResult testResult = new TestResult(Integer.parseInt(id), status);

        bookingService.updateStatus(testResult);

        return myBookings(model);
    }

    @PostMapping(value = "/mybookings", params = "cancelVaccine")
    private String myBookings(Model model, String id, String status, String type) {

        Vaccine vaccine = new Vaccine(Integer.parseInt(id), status, type);

        bookingService.updateStatus(vaccine);

        return myBookings(model);
    }

    /**
     * Returns a Tuple2 object with two Booleans.
     *
     * The first Boolean is true if the user has any bookings of type TEST.
     * The second Boolean is true is the user ahas any bookings of type VACCINE.
     *
     * @param bookingList list of a specific user's bookings
     * @return Tuple2<Boolean, Boolean>
     */
    private Tuple2<Boolean, Boolean> userHasBooking(List<Tuple3<Booking, BookingType, Boolean>> bookingList) {

        boolean hasTestBooking = false;
        boolean hasVaccineBooking = false;

        for (Tuple3<Booking, BookingType, Boolean> booking : bookingList ) {
            if (!hasTestBooking)
                if (booking.getFirst().getType().equals("TEST")) hasTestBooking = true;

            if (!hasVaccineBooking)
                if (booking.getFirst().getType().equals("VACCINE")) hasVaccineBooking = true;

            if (hasTestBooking && hasVaccineBooking)
                break;
        }

        return new Tuple2<>(hasTestBooking, hasVaccineBooking);
    }

    private void filterBookings(List<Tuple3<Booking, BookingType, Boolean>> bookingList) {
        bookingList.removeIf(booking -> booking.getSecond().getStatus().equals("CANCELLED"));
    }

    private boolean canCancel(List<Tuple3<Booking, BookingType, Boolean>> bookingList, String type) {

        for (Tuple3<Booking, BookingType, Boolean> booking : bookingList)
            if (booking.getFirst().getType().equals(type))
                if (booking.getThird()) return true;

        return false;
    }
}
