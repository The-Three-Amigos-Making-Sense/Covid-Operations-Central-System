package com.thethreeamigosmakingsense.covidoperationscentralsystem.Service;

import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.Authority;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.User;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Repository.UserRepository;
import groovy.lang.Tuple2;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean signupUser(User user) {

        if (hasInvValidCPR(user)) return false;
        if (hasInvalidPhoneNo(user)) return false;
        if (hasEmptyFields(user)) return false;
        if (hasEmptyPassword(user)) return false;
        if (hasInvalidEmail(user)) return false;

        return userRepository.saveNewUser(user);
    }

    public boolean updateUser(User user) {
        if (hasInvalidEmail(user)) return false;
        if (hasInvalidPhoneNo(user)) return false;
        if (hasEmptyFields(user)) return false;

        return userRepository.updateRemoteUser(user);
    }

    public void updateAuthority(Authority authority) {
        userRepository.updateAuthority(authority);
    }

    public void changeEnabledUser(User user) {
        userRepository.changeEnabledUser(user);
    }

    public User fetchUser(String username) {
        return userRepository.fetchUser(username);
    }

    public Authority fetchAuthority(User user) {
        return userRepository.fetchAuthority(user);
    }

    public User fetchRemoteUser() {
        return userRepository.fetchRemoteUser();
    }

    public List<User> fetchAllUsers() {
        return userRepository.fetchAllUsers();
    }

    public List<Tuple2<User, Authority>> fetchAllUsersAndAuthorities() {
        List<Tuple2<User, Authority>> userList = new ArrayList<>();

        for (User user : fetchAllUsers()) {
            userList.add(new Tuple2<>(user, userRepository.fetchAuthority(user)));
        }

        return userList;
    }

    public List<Tuple2<User, Authority>> searchAllUsers(String searchTerm) {

        List<Tuple2<User, Authority>> userList = new ArrayList<>();

        for (User user : userRepository.searchAllUsers(searchTerm)) {
            userList.add(new Tuple2<>(user, userRepository.fetchAuthority(user)));
        }

        return userList;
    }

    public String checkPrivilege(String username) {
        return userRepository.getAuthority(username);
    }

    private boolean hasEmptyFields(User user) {
        if (user.getEmail().isBlank())     return true;
        if (user.getFirstname().isBlank()) return true;
        if (user.getLastname().isBlank())  return true;
        else                               return false;
    }

    private boolean hasEmptyPassword(User user) {
        return user.getPassword().isBlank();
    }

    public boolean hasInvalidEmail(User user) {
        EmailValidator validator = EmailValidator.getInstance();
        return !validator.isValid(user.getEmail());
    }

    private boolean hasInvalidPhoneNo(User user) {

        try {
            Integer.parseInt(user.getPhone_no());
        } catch (Exception e) {
            return true;
        }
        return user.getPhone_no().length() != 8;
    }

    private boolean hasInvValidCPR(User user) {

        String cpr = user.getUsername();

        try {
            Integer.parseInt(cpr.substring(0,6));
            Integer.parseInt(cpr.substring(7,11));
        } catch (Exception e) {
            return true;
        }
        return cpr.charAt(6) != '-';
    }
}
