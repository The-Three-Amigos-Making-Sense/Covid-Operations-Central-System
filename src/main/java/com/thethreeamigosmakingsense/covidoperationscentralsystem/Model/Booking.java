package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

import java.time.LocalDateTime;

public class Booking {

    private Integer booking_id;
    private String username;
    private String date;
    private String time;
    private String type;

    public Booking() {
    }

    public Booking(Integer booking_id, String username, String date, String time, String type) {
        this.booking_id = booking_id;
        this.username = username;
        this.date = date;
        this.time = time;
        this.type = type;
    }

    public Integer getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Integer booking_id) {
        this.booking_id = booking_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public LocalDateTime getLocalDateTime() {
        int year = Integer.parseInt(date.substring(6));
        int month = Integer.parseInt(date.substring(3, 5));
        int day = Integer.parseInt(date.substring(0, 2));
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3));

        return LocalDateTime.of(year, month, day, hour, minute, 0, 0);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConcatTime() {
        return time.substring(0, 2) + time.substring(3);
    }

    public boolean equals(Booking booking) {
        return
                this.date.equals(booking.getDate()) &&
                this.time.equals(booking.getTime()) &&
                this.type.equals(booking.getType());
    }
}
