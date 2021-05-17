package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    Booking booking = new Booking("test", "12-12-1212", "12:00", "TEST");

    @Test
    void getConcatTime() {
        assertEquals("1200", booking.getConcatTime());
    }
}