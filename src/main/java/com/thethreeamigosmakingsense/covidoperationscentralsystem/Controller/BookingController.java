package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookingController {

    @Autowired
    BookingService bookingService;

    @GetMapping("/booking")
    public String booking(Model model) {

        model.addAttribute("times", bookingService.getTimes());

        return "booking/booking";
    }

    @PostMapping("/booking")
    public String booking(Booking booking) {

        if (bookingService.newBooking(booking)) {
            return "signup/success";
        } else return "ADKFSADLFJASF";
    }
}
