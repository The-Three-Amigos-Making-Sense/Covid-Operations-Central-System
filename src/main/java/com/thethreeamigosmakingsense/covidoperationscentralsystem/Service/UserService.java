package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean signupUser(User user) {

        if (!checkValidCPR(user)) return false;
        if (!checkValidPhoneNo(user)) return false;
        if (!checkEmptyFields(user)) return false;

        return userRepository.saveNewUser(user);
    }

    private boolean checkEmptyFields(User user) {
        if (user.getEmail().isBlank())     return false;
        if (user.getFirstname().isBlank()) return false;
        if (user.getLastname().isBlank())  return false;
        if (user.getPassword().isBlank())  return false;
        else                               return true;
    }

    private boolean checkValidPhoneNo(User user) {

        try {
            Integer.parseInt(user.getPhone_no());
        } catch (Exception e) {
            return false;
        }
        return user.getPhone_no().length() == 8;
    }

    private boolean checkValidCPR(User user) {

        String cpr = user.getUsername();

        try {
            Integer.parseInt(cpr.substring(0,6));
            Integer.parseInt(cpr.substring(7,11));
        } catch (Exception e) {
            return false;
        }

        if (cpr.charAt(6) != '-') return false;

        return true;
    }
}
