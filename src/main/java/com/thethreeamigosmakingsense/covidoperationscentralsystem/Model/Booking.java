package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {

    private int booking_id;
    private String username;
    private LocalDate date;
    private LocalTime time;
    private String type;

    public Booking(int booking_id, String username, LocalDate date, LocalTime time, String type) {
        this.booking_id = booking_id;
        this.username = username;
        this.date = date;
        this.time = time;
        this.type = type;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
