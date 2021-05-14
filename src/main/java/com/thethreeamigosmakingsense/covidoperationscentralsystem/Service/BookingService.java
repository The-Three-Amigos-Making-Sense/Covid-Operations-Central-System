package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    public boolean newBooking(Booking booking) {
        return bookingRepository.createBooking(booking);
    }

    public TreeMap<String, Boolean> getTimes() {
        List<Booking> bookingsList = bookingRepository.fetchAllBookings();

        int openingHour = 9;
        int openingMinute = 0;
        int timePerTest = 10;
        int hour;
        int minute;
        boolean available = true;
        String time;

        TreeMap<String, Boolean> times = new TreeMap<>();
        for (hour = openingHour, minute = openingMinute; hour < 19; minute += timePerTest) {

            if (minute >= 60) {
                hour++;
                minute = 0;
            }

            String sHour = Integer.toString(hour);
            String sMinute = Integer.toString(minute);
            if (hour < 10) sHour = "0" + hour;
            if (minute < 10) sMinute = "0" + minute;

            time = sHour + ":" + sMinute;
            times.put(time, available);
        }

        return times;
    }
}
