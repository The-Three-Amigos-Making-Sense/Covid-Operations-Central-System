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
            case 418 -> { // Error Code: 418. I'm a teapot
                error = "418. I'm a teapot";
                msg = "Warning: I'm hot!";
            }
            case 425 -> { // Error Code: 425. Too early
                error = "425. Too early";
                msg = "You have attempted to book your second vaccine earlier than 24 days from your first shot";
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
