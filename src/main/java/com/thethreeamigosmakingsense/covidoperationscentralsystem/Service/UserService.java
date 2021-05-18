package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean signupUser(User user) {

        if (!checkValidCPR(user)) return false;
        if (!checkValidPhoneNo(user)) return false;
        if (!checkEmptyFields(user)) return false;
        if (!checkEmptyPassword(user)) return false;
        if (!checkValidEmail(user)) return false;

        return userRepository.saveNewUser(user);
    }

    public boolean updateUser(User user) {
        if (!checkValidEmail(user)) return false;
        if (!checkValidPhoneNo(user)) return false;
        if (!checkEmptyFields(user)) return false;

        return userRepository.updateUser(user);
    }

    public List<User> fetchAllUsersWithRole(String role) {
        return userRepository.fetchAllUsersWithRole(role);
    }

    public List<User> searchAllUsers(String role, String searchTerm) {
        return userRepository.searchAllUsersWithRole(role, searchTerm);
    }

    private boolean checkEmptyFields(User user) {
        if (user.getEmail().isBlank())     return false;
        if (user.getFirstname().isBlank()) return false;
        if (user.getLastname().isBlank())  return false;
        else                               return true;
    }

    private boolean checkEmptyPassword(User user) {
        return user.getPassword().isBlank();
    }

    private boolean checkValidEmail(User user) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(user.getEmail());
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
