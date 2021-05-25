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

        return LocalDateTime.of(year, month, day, hour, minute);
    }

    public void setDateTime(LocalDateTime ldt) {

        String day;
        if (ldt.getDayOfMonth() < 10) day = "0" + ldt.getDayOfMonth();
        else day = String.valueOf(ldt.getDayOfMonth());

        String month;
        if (ldt.getMonthValue() < 10) month = "0" + ldt.getMonthValue();
        else month = String.valueOf(ldt.getMonthValue());

        String year = String.valueOf(ldt.getYear());

        String hour;
        if (ldt.getHour() < 10) hour = "0" + ldt.getHour();
        else hour = String.valueOf(ldt.getHour());

        String minute;
        if (ldt.getMinute() < 10) minute = "0" + ldt.getMinute();
        else minute = String.valueOf(ldt.getMinute());

        date = day + "-" + month + "-" + year;
        time = hour + ":" + minute;
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
