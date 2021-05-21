package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.BookingType;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.BookingRepository;
import groovy.lang.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    HttpServletRequest http;

    public List<Tuple2<Booking, BookingType>> fetchUsersBoookings(String username) {

        List<Tuple2<Booking, BookingType>> bookingList = new ArrayList<>();

        for (Booking booking : bookingRepository.fetchUsersBookings(username)) {
            BookingType type = bookingRepository.fetchStatus(booking);
            bookingList.add(new Tuple2<>(booking, type));
        }

        return bookingList;
    }

    public boolean userHasActiveBooking(String username, String type) {

        List<Booking> bookingList = bookingRepository.fetchUsersBookings(username);

        if (bookingList.isEmpty()) return false;

        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookingList) {

            if (booking.getLocalDateTime().isAfter(now) &&
                    type.equals(booking.getType()))
                return true;
        }
        return false;
    }

    /**
     *
     * @param booking is the new booking a user is attempting to reserve
     * @return true or false depending on whether the booking has been saved to the database
     */
    public boolean newBooking(Booking booking) {

        // Checks if the user already has an active booking of that type
        if (userHasActiveBooking(http.getRemoteUser(), booking.getType())) return false;

        // Checks if the user is attempting to book a time that is not at the 10 minute interval
        if (Integer.parseInt(booking.getTime().substring(3,5)) % 10 != 0) return false;

        // Checks if the time has already been booked for that type of test
        for (Booking booked : bookingRepository.fetchAllBookings(booking.getType()))
            if (booked.equals(booking)) return false;

        return bookingRepository.createBooking(booking);
    }

    /**
     *
     * @param date is the selected date on the booking page
     * @param type is the type of booking (test or vaccine)
     * @return List of available time on the selected day that are more than one hour from now.
     */
    public List<String> getAvailableTimes(String date, String type) {

        List<Booking> bookingsList = bookingRepository.fetchAllBookings(type);

        // Creates String of today's date and a concatenated integer of current hour and minute for later use.
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String today = LocalDateTime.now().format(formatter);
        int nowHour = now.getHour();
        int nowMinute = now.getMinute();
        String sNowHour = Integer.toString(nowHour);
        String sNowMinute = Integer.toString(nowMinute);
        int nowTime = Integer.parseInt(sNowHour+sNowMinute);

        // Initializes variables for a series of checks
        int openingHour = 9;
        int openingMinute = 0;
        int timePerTest = 10;
        int hour;
        int minute;
        String sHour;
        String sMinute;
        String time;

        // List for storing available times on given date
        List<String> times = new ArrayList<>();

        // Loop checks for availability of every timeslot and saves available times to list
        addTimes:
        for (hour = openingHour, minute = openingMinute ;; minute += timePerTest) {

            if (minute >= 60) {
                hour++;
                minute = 0;
            }

            if (hour == 19) break;

            // Saves hour and minutes to Strings to ensure they will always be in two digits
            sHour = Integer.toString(hour);
            sMinute = Integer.toString(minute);
            if (hour < 10) sHour = "0" + hour;
            if (minute < 10) sMinute = "0" + minute;

            // Skips the timeslot if it is earlier than one hour from now
            if (date.equals(today) && nowTime > Integer.parseInt(sHour+sMinute)-100 ) continue;

            time = sHour + ":" + sMinute;

            // Skips the timeslot if is unavailable
            for (Booking booking : bookingsList) {
                if (date.equals(booking.getDate())) {
                    if (time.equals(booking.getTime())) {
                        continue addTimes;
                    }
                }
            }

            times.add(time);
        }

        return times;
    }
}
