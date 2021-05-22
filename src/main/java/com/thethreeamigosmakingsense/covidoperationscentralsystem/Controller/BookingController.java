package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    HttpServletRequest http;

    @GetMapping("/test")
    private String test(Model model) {

        model.addAttribute("navItem", "test");
        model.addAttribute("type", "covid-19 test");
        model.addAttribute("date", getCurrentDate());
        model.addAttribute("times", bookingService.getAvailableTimes(getCurrentDate(), "TEST"));

        if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "TEST"))
            return "booking/userHasBooking";
        else return "booking/test";
    }

    @PostMapping("/test")
    private String fetchTestAvailability(Model model, String date) {

        model.addAttribute("navItem", "test");
        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "TEST"));

        if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "TEST"))
            return "booking/userHasBooking";
        else return "booking/test";
    }

    @GetMapping("/vaccine")
    private String vaccine(Model model) {

        model.addAttribute("navItem", "vaccine");
        model.addAttribute("type", "covid-19 vaccine");
        model.addAttribute("date", getCurrentDate());
        model.addAttribute("times", bookingService.getAvailableTimes(getCurrentDate(), "VACCINE"));

        if (bookingService.userHasActiveBookingOfType(http.getRemoteUser(), "VACCINE"))
            return "booking/userHasBooking";
        else return "booking/vaccine";
    }

    @PostMapping("/vaccine")
    private String fetchVaccineAvailability(Model model, String date) {

        model.addAttribute("navItem", "vaccine");
        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "VACCINE"));

        return "booking/vaccine";
    }

    @PostMapping("/booked")
    private String test(Model model, Booking booking) {

        if (booking.getType().equals("TEST")) {
            model.addAttribute("navItem", "test");
            model.addAttribute("type", "covid-19 test");
        }
        else if (booking.getType().equals("VACCINE")) {
            model.addAttribute("navItem", "vaccine");
            model.addAttribute("type", "covid-19 vaccine");
        }

        if (bookingService.newBooking(booking)) {
            return "booking/success";
        } else return "redirect:/test?error";
    }

    private String getCurrentDate() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(formatter);
    }

}
