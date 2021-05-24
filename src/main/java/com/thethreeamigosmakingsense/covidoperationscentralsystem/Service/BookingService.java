package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Booking;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.BookingType;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.BookingRepository;
import groovy.lang.Tuple3;
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

    public Booking fetchBookingByID(int id) {
        return bookingRepository.fetchBookingByID(id);
    }

    public void updateStatus(BookingType bookingtype) {
        bookingRepository.updateStatus(bookingtype);
    }

    /**
     * Returns a List of Tuple3s with information relevant to a specific user's bookings
     *
     * @param username ID of user whose bookings are being fetched
     * @return a Tuple3<Booking, BookingType, Boolean
     */
    public List<Tuple3<Booking, BookingType, Boolean>> fetchUsersBoookings(String username) {

        List<Tuple3<Booking, BookingType, Boolean>> bookingList = new ArrayList<>();

        for (Booking booking : bookingRepository.fetchUsersBookings(username)) {

            BookingType type = bookingRepository.fetchStatus(booking);

            LocalDateTime bookingTime = booking.getLocalDateTime();
            LocalDateTime now = LocalDateTime.now();

            // A booking can be cancelled if has not yet taken place
            boolean canBeCancelled =
                            bookingTime.isAfter(now) &&
                            (type.getStatus().equals("PENDING") ||
                            type.getStatus().equals("TEST_PENDING"));

            bookingList.add(new Tuple3<>(booking, type, canBeCancelled));
        }

        return bookingList;
    }

    public boolean userHasActiveBookingOfType(String username, String type) {

        List<Booking> bookingList = bookingRepository.fetchUsersBookings(username);

        if (bookingList.isEmpty()) return false;

        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookingList) {

            if (booking.getLocalDateTime().isAfter(now) &&
                    type.equals(booking.getType()) &&
                    !bookingRepository.fetchStatus(booking).getStatus().equals("CANCELLED"))
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
        if (userHasActiveBookingOfType(http.getRemoteUser(), booking.getType())) return false;

        // Checks if the user is attempting to book a time that is not at the 10 minute interval
        if (Integer.parseInt(booking.getTime().substring(3,5)) % 10 != 0) return false;

        // Checks if the time has already been booked for that type of test
        for (Booking booked : bookingRepository.fetchAllBookingsByType(booking.getType())) {
            if (bookingRepository.fetchStatus(booked).getStatus().equals("CANCELLED")) continue;
            if (booked.equals(booking)) return false;
        }

        return bookingRepository.createBooking(booking);
    }

    /**
     *
     * @param selectedDate is the selected selectedDate on the booking page
     * @param type is the type of booking (test or vaccine)
     * @return List of available time on the selected day that are more than one hour from now.
     */
    public List<String> getAvailableTimes(String selectedDate, String type) {

        // Initializes variables for a series of checks
        List<Booking> bookingsList = bookingRepository.fetchAllBookingsByType(type);
        LocalDateTime now = LocalDateTime.now();
        int openingHour = 9;
        int openingMinute = 0;
        int timePerTest = 10;
        int hour;
        int minute;
        String sHour;
        String sMinute;
        String time;

        // List for storing available times on given selectedDate
        List<String> times = new ArrayList<>();

        // Loop checks for availability of every timeslot and saves available times to list
        addTimes:
        for (hour = openingHour, minute = openingMinute ;; minute += timePerTest) {

            if (minute >= 60) {
                hour++;
                minute = 0;
            }

            if (hour == 19) break;

            // Skips the timeslot if it is earlier than one hour from now
            int selectedDay = Integer.parseInt(selectedDate.substring(0, 2));
            int selectedMonth = Integer.parseInt(selectedDate.substring(3, 5));
            int selectedYear = Integer.parseInt(selectedDate.substring(6, 10));
            LocalDateTime selected = LocalDateTime.of(selectedYear, selectedMonth, selectedDay, hour, minute);
            if (selected.isBefore(now.plusHours(1))) continue;

            // Skips the timeslot if is unavailable. Checks for cancelled times first.
            for (Booking booking : bookingsList) {
                BookingType bookingType = bookingRepository.fetchStatus(booking);
                if (bookingType.getStatus().equals("CANCELLED")) break;

                if (booking.getLocalDateTime().isEqual(selected)) continue addTimes;
            }

            // Saves hour and minutes to Strings to ensure they will always be in two digits
            sHour = Integer.toString(hour);
            sMinute = Integer.toString(minute);
            if (hour < 10) sHour = "0" + hour;
            if (minute < 10) sMinute = "0" + minute;

            time = sHour + ":" + sMinute;

            times.add(time);
        }

        return times;
    }
}
