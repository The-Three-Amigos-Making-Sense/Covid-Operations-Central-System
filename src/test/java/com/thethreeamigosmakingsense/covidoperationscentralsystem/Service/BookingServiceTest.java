package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import static org.junit.jupiter.api.Assertions.*;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

    static BookingService bookingService;
    static User user;

    @BeforeAll
    static void instantiate() {
        bookingService = new BookingService();
    }

}