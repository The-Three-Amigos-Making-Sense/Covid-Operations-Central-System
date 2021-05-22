package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class Vaccine implements BookingType {

    private Integer booking_id;
    private String status;
    private String type;

    public Vaccine() {}

    public Vaccine(Integer booking_id, String status, String type) {
        this.booking_id = booking_id;
        this.status = status;
        this.type = type;
    }

    @Override
    public Integer getBooking_id() {
        return booking_id;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
