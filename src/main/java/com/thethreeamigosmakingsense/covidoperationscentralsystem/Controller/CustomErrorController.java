package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String error(Model model, HttpServletRequest http) {

        int errorCode = (int) http.getAttribute("javax.servlet.error.status_code");
        String error;
        String msg;

        switch (errorCode) {
            case 400 -> { // Http Error Code: 400. Bad Request
                error = "400. Bad Request";
                msg = "You have attempted to send bad data";
            }
            case 401 -> { // Http Error Code: 401. Unauthorized
                error = "401. Unauthorized";
                msg = "You do not have permission to see this page";
            }
            case 404 -> { // Http Error Code: 404. Resource not found
                error = "404. Resource not found";
                msg = "This is not the page you're looking for";
            }
            case 409 -> { // Error Code: 409. Already has Test Booking
                error = "ERROR";
                msg = "You already have one active booking to get a covid-19 test";
            }
            case 410 -> { // Error Code: 410. Already has Vaccine Booking
                error = "ERROR";
                msg = "You already have one active booking to get a covid-19 vaccine";
            }
            case 411 -> {
                error = "ERROR";
                msg = "You have already received two shots of the covid-19 vaccine. You cannot receive anymore";
            }
            case 418 -> { // Error Code: 418. I'm a teapot
                error = "418. I'm a teapot";
                msg = "Warning: I'm hot!";
            }
            case 500 -> { // Http Error Code: 500. Internal Server Error
                error = "500. Internal Server Error";
                msg = "Something went wrong. What did you do?";
            }
            default -> { //
                error = "Unknown Error";
                msg = "If you are seeing this error something has gone very very wrong";
            }
        }

        model.addAttribute("error", error);
        model.addAttribute("msg", msg);

        return "error/error";
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    @GetMapping("/teapot")
    public void teapot() {
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
    }
}
