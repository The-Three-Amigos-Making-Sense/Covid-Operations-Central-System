package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class TestResult implements BookingType {

    private int booking_id;
    private String status;

    public TestResult() {}

    public TestResult(int booking_id, String status) {
        this.booking_id = booking_id;
        this.status = status;
    }

    public int getBooking_id() {
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
}
