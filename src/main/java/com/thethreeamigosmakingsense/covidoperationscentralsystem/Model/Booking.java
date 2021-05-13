package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {

    int booking_id;
    int user_cpr;
    LocalDate date;
    LocalTime time;
    String type;

    public Booking(int booking_id, int user_cpr, LocalDate date, LocalTime time, String type) {
        this.booking_id = booking_id;
        this.user_cpr = user_cpr;
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

    public int getUser_cpr() {
        return user_cpr;
    }

    public void setUser_cpr(int user_cpr) {
        this.user_cpr = user_cpr;
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
