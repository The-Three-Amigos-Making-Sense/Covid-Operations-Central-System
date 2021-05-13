package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService userService = new UserService();

    User user = new User("121212-1212", "asdf@adf.com", "John",
            "Doe", "12345678", "asdf");


    @Test
    void checkEmptyFields() {

        assertTrue(userService.checkEmptyFields(user));
        assertTrue(userService.checkValidPhoneNo(user));
    }

    @Test
    void checkValidCPR() {
        assertFalse(userService.checkValidCPR(user));
        user.setUsername("1234567890");
        assertFalse(userService.checkValidCPR(user));
        user.setUsername("123456-7890");
        assertTrue(userService.checkValidCPR(user));
    }
}