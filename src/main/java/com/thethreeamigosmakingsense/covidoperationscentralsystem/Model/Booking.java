package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class Booking {

    private int booking_id;
    private String username;
    private String date;
    private String time;
    private String type;

    public Booking() {
    }

    public Booking(int booking_id, String username, String date, String time, String type) {
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

    public String getDate() {
        return date;
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
}
