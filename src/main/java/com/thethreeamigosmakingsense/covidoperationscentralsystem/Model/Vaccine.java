package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class Vaccine {

    private int booking_id;
    private String type;

    public Vaccine(int booking_id, String type) {
        this.booking_id = booking_id;
        this.type = type;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
