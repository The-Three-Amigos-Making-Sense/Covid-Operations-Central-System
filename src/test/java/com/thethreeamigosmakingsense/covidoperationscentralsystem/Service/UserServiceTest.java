package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService userService = new UserService();

    User user = new User("1234", "ihavealongemailaddress@gmail.com", "Hans", "Pedesern", "12345678", "");

    @Test
    void checkValidEmail() {
        assertFalse(userService.hasInvalidEmail(user));
    }
}