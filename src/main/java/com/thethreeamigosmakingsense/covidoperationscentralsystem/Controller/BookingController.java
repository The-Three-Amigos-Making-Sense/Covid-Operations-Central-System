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

    @GetMapping("/test")
    private String test(Model model) {

        model.addAttribute("navItem", "test");
        model.addAttribute("type", "covid-19 test");
        model.addAttribute("date", getCurrentDate());
        model.addAttribute("times", bookingService.getAvailableTimes(getCurrentDate(), "TEST"));

        if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "TEST"))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        else return "booking/test";
    }

    @PostMapping("/test")
    private String fetchTestAvailability(Model model, String date) {

        model.addAttribute("navItem", "test");
        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "TEST"));

        if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "TEST"))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        else return "booking/test";
    }

    @GetMapping("/vaccine")
    private String vaccine(Model model) {

        model.addAttribute("navItem", "vaccine");
        model.addAttribute("type", "covid-19 vaccine");
        model.addAttribute("date", getCurrentDate());
        model.addAttribute("times", bookingService.getAvailableTimes(getCurrentDate(), "VACCINE"));

        if (bookingService.userHasSecondShot(http.getRemoteUser()))
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);
        else if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "VACCINE"))
            throw new ResponseStatusException(HttpStatus.GONE);
        else return "booking/vaccine";
    }

    @PostMapping("/vaccine")
    private String fetchVaccineAvailability(Model model, String date) {

        model.addAttribute("navItem", "vaccine");
        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "VACCINE"));

        if (bookingService.userHasSecondShot(http.getRemoteUser()))
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);
        else if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "VACCINE"))
            throw new ResponseStatusException(HttpStatus.GONE);
        else return "booking/vaccine";
    }

    @PostMapping("/booked")
    private String test(Model model, Booking booking) {

        booking.setUsername(http.getRemoteUser()); // in case a sneaky user attempts to book on behalf of someone else

        String type;

        if (booking.getType().equals("TEST")) {
            model.addAttribute("navItem", "test");
            model.addAttribute("type", "covid-19 test");
            type = "test";

        } else if (booking.getType().equals("VACCINE")) {
            model.addAttribute("navItem", "vaccine");
            model.addAttribute("type", "covid-19 vaccine");
            type = "vaccine";

        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (bookingService.newBooking(booking)) {
            return "booking/success";
        } else return "redirect:/" + type + "?error";
    }

    private String getCurrentDate() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(formatter);
    }

}
