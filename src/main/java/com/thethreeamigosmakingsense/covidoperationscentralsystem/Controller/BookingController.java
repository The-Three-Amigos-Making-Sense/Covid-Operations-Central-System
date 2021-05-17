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

    @GetMapping("/booking")
    public String booking(Model model) {

        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = localDate.format(formatter);


        model.addAttribute("type", "covid-19 test");
        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "TEST"));

        if (bookingService.userHasActiveBooking(http.getRemoteUser(), "TEST"))
            return "booking/userHasBooking";
        else return "booking/booking";
    }

    @PostMapping("/booked")
    public String booking(Model model, Booking booking) {

        if (booking.getType().equals("TEST"))
            model.addAttribute("type", "covid-19 test");
        else if (booking.getType().equals("VACCINE"))
            model.addAttribute("type", "covid-19 vaccine");

        if (bookingService.newBooking(booking)) {
            return "booking/success";
        } else return "redirect:/booking?error";
    }

    @PostMapping("/booking")
    public String fetchAvailability(Model model, String date) {

        model.addAttribute("date", date);
        model.addAttribute("times", bookingService.getAvailableTimes(date, "TEST"));

        return "booking/booking";
    }
}
