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

        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        User user = userService.fetchUser(username);
        Authority authority = userService.fetchAuthority(user);
        List<Tuple3<Booking, BookingType, Boolean>> bookingList = bookingService.fetchUsersBoookings(username);
        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList);

        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        String[] vaccineStatus = {"PENDING", "RECEIVED", "CANCELLED"};
        String[] testStatus = {"TEST_PENDING", "RESULT_PENDING", "NEGATIVE", "POSITIVE", "CANCELLED"};
        String[] authorities = {"ROLE_USER", "ROLE_PERSONNEL", "ROLE_ADMIN"};

        model.addAttribute("isRemoteUser", username.equals(http.getRemoteUser()));
        model.addAttribute("vaccineStatus", vaccineStatus);
        model.addAttribute("testStatus", testStatus);
        model.addAttribute("hasTestBooking", hasBookings.getFirst());
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond());
        model.addAttribute("user", user);
        model.addAttribute("authority", authority);
        model.addAttribute("authorityStrings", authorities);
        model.addAttribute("bookings", bookingList);

        return "profile/user";
    }

    @PostMapping(value = "/user/{username}", params = "updateTest")
    private String updateTest(@PathVariable("username") String username, Model model, String id,
                              @RequestParam(value = "updateTest") String status) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

        TestResult testResult = new TestResult(Integer.parseInt(id), status);

        bookingService.updateStatus(testResult);

        return readUserInfo(username, model);
    }

    @PostMapping(value = "/user/{username}", params = "updateVaccine")
    private String updateVaccine(@PathVariable("username") String username, Model model, String id,
                                 @RequestParam(value = "updateVaccine") String status, String type) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

        Vaccine vaccine = new Vaccine(Integer.parseInt(id), status, type);

        bookingService.updateStatus(vaccine);

        if (vaccine.getType().equals("FIRST_SHOT") && vaccine.getStatus().equals("RECEIVED")) {
            bookingService.autoBookSecondShot(username, vaccine);
        }

        return readUserInfo(username, model);
    }

    @PostMapping(value = "/user/{username}", params = "updateAuthority")
    private String updateAuthority(@PathVariable("username") String username, Model model,
                                   @RequestParam(value="updateAuthority") String authority) {

        switch (authority) {
            case "USER" -> authority = "ROLE_USER";
            case "PERSONNEL" -> authority = "ROLE_PERSONNEL";
            case "ADMIN" -> authority = "ROLE_ADMIN";
        }

        Authority auth = new Authority();
        auth.setUsername(username);
        auth.setAuthority(authority);

        userService.updateAuthority(auth);

        return readUserInfo(username, model);
    }

    @GetMapping(value = "/mybookings")
    private String myBookings(Model model){

        List<Tuple3<Booking, BookingType, Boolean>> bookingList =
                bookingService.fetchUsersBoookings(http.getRemoteUser());

        filterBookings(bookingList);

        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList);

        model.addAttribute("navItem", "mybookings");
        model.addAttribute("hasTestBooking", hasBookings.getFirst());     // if false the table is never rendered
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond()); // if false the table is never rendered
        model.addAttribute("bookings", bookingList);
        model.addAttribute("user", new User()); // is never used, prevents page fragment from throwing error
        return "profile/myBookings";
    }

    @PostMapping(value = "/mybookings", params = "updateTest")
    private String myBookings(Model model, String id,
                              @RequestParam(value = "updateTest") String status) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

        TestResult testResult = new TestResult(Integer.parseInt(id), status);

        bookingService.updateStatus(testResult);

        return myBookings(model);
    }

    @PostMapping(value = "/mybookings", params = "updateVaccine")
    private String myBookings(Model model, String id,
                              @RequestParam(value = "updateVaccine") String status, String type) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

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
}
