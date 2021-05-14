package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

    static BookingService bookingService;

    @BeforeAll
    static void instantiate() {
        bookingService = new BookingService();
    }

    @Test
    void getTimes() {
        //bookingService.getTimes();
    }
}