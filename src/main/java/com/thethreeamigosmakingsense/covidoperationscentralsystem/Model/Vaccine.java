package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class Vaccine implements BookingType {

    private int booking_id;
    private String type;
    private String status;

    public Vaccine() {}

    public Vaccine(int booking_id, String type, String status) {
        this.booking_id = booking_id;
        this.type = type;
        this.status = status;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}
