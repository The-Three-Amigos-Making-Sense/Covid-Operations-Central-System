package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class TestResult implements BookingType {

    private Integer booking_id;
    private String status;

    public TestResult() {}

    public TestResult(Integer booking_id, String status) {
        this.booking_id = booking_id;
        this.status = status;
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
}
