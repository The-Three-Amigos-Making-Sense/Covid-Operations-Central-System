package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HttpServletRequest http;

    /**
     * GetMapping for test booking page
     * @param model model
     * @return html
     */
    @GetMapping("/test")
    private String test(Model model) {

        model.addAttribute("navItem", "test"); // active menu
        model.addAttribute("type", "covid-19 test"); //
        model.addAttribute("date", getCurrentDate()); // default date set on the datepicker
        model.addAttribute("times", bookingService.getAvailableTimes(getCurrentDate(), "TEST")); // gets list of available times for the dropdown

        // throws error if user already has an active booking. The error is resolved in CustomErrorController
        if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "TEST"))
            throw new ResponseStatusException(HttpStatus.CONFLICT); // 409

        else return "booking/test";
    }

    /**
     * PostMapping changing date on the datepicker
     * @param model model
     * @param date picked date.
     * @return html
     */
    @PostMapping("/test")
    private String fetchTestAvailability(Model model, String date) {

        model.addAttribute("navItem", "test"); // active menu
        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "TEST")); // gets list of available times for the dropdown

        // throws error if user already has an active booking. The error is resolved in CustomErrorController
        if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "TEST"))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        else return "booking/test";
    }

    /**
     * GetMapping for the vaccine booking page
     * @param model model
     * @return html
     */
    @GetMapping("/vaccine")
    private String vaccine(Model model) {

        model.addAttribute("navItem", "vaccine"); // active menu
        model.addAttribute("type", "covid-19 vaccine");
        model.addAttribute("date", getCurrentDate()); // default date set on the datepicker
        model.addAttribute("times", bookingService.getAvailableTimes(getCurrentDate(), "VACCINE")); // gets list of available times for the dropdown

        // throws error if user is already fully vaccinated. The error is resolved in CustomErrorController
        if (bookingService.userHasSecondShot(http.getRemoteUser()))
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED); // 411

        // throws error if user already has an active booking. The error is resolved in CustomErrorController
        else if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "VACCINE"))
            throw new ResponseStatusException(HttpStatus.GONE); // 410

        else return "booking/vaccine";
    }

    /**
     * PostMapping for changing date on the date picker
     * @param model model
     * @param date selected date on the date picker
     * @return
     */
    @PostMapping("/vaccine")
    private String fetchVaccineAvailability(Model model, String date) {

        model.addAttribute("navItem", "vaccine"); // active menu
        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "VACCINE")); // gets list of available times for the dropdown

        // throws error if user is already fully vaccinated. The error is resolved in CustomErrorController
        if (bookingService.userHasSecondShot(http.getRemoteUser()))
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);

            // throws error if user already has an active booking. The error is resolved in CustomErrorController
        else if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "VACCINE"))
            throw new ResponseStatusException(HttpStatus.GONE);

        else return "booking/vaccine";
    }

    /**
     * PostMapping for booking a test or vaccine
     * @param model model
     * @param booking object
     * @return
     */
    @PostMapping("/booked")
    private String book(Model model, Booking booking) {

        booking.setUsername(http.getRemoteUser()); // in case a sneaky user attempts to book on behalf of someone else

        String type;

        if (booking.getType().equals("TEST")) {
            model.addAttribute("navItem", "test"); // active menu
            model.addAttribute("type", "covid-19 test"); // title variable
            type = "test";

        } else if (booking.getType().equals("VACCINE")) {
            model.addAttribute("navItem", "vaccine"); // active menu
            model.addAttribute("type", "covid-19 vaccine"); // title variable
            type = "vaccine";

        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // if bad type

        if (bookingService.newBooking(booking)) { // save booking in database
            return "booking/success";
        } else return "redirect:/" + type + "?error";
    }

    /**
     * Used for the GetMapping methods to show current date
     * @return formatted string of current date
     */
    private String getCurrentDate() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(formatter);
    }

}
