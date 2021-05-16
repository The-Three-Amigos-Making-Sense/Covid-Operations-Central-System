package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

@Controller
public class BookingController {

    @Autowired
    BookingService bookingService;

    @GetMapping("/booking")
    public String booking(Model model, String selectedDate) {

        System.out.println(selectedDate);
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dateString = date.format(formatter);

        model.addAttribute("times", bookingService.getTimes());

        return "booking/booking";
    }

    @PostMapping("/booking")
    public String booking(Booking booking) {

        if (bookingService.newBooking(booking)) {
            return "booking/success";
        } else return "redirect:/booking?error";
    }
}
