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

    /**
     * Does backend checks and connects to repository
     * @param user object to be saved to database
     * @return boolean if correctly saved
     */
    public boolean signupUser(User user) {

        if (hasInvalidCPR(user)) return false;
        if (hasInvalidPhoneNo(user)) return false;
        if (hasEmptyFields(user)) return false;
        if (hasEmptyPassword(user)) return false;
        if (hasInvalidEmail(user)) return false;

        return userRepository.saveNewUser(user);
    }

    /**
     * Does backend checks and connects to repository
     * @param user object for to updated in database
     * @return boolean if correctly updated
     */
    public boolean updateUser(User user) {
        if (hasInvalidEmail(user)) return false;
        if (hasInvalidPhoneNo(user)) return false;
        if (hasEmptyFields(user)) return false;

        return userRepository.updateUser(user);
    }

    // connects to repository
    public void updateAuthority(Authority authority) {
        userRepository.updateAuthority(authority);
    }

    // connects to repository
    public void changeEnabledUser(User user) {
        userRepository.changeEnabledUser(user);
    }

    // connects to repository
    public User fetchUser(String username) {
        return userRepository.fetchUser(username);
    }

    // connects to repository
    public Authority fetchAuthority(User user) {
        return userRepository.fetchAuthority(user);
    }

    // connects to repository
    public User fetchRemoteUser() {
        return userRepository.fetchRemoteUser();
    }

    // connects to repository
    public List<User> fetchAllUsers() {
        return userRepository.fetchAllUsers();
    }

    /**
     * @return List of users and their belonging authority objects
     */
    public List<Tuple2<User, Authority>> fetchAllUsersAndAuthorities() {
        List<Tuple2<User, Authority>> userList = new ArrayList<>();

        for (User user : fetchAllUsers()) {
            userList.add(new Tuple2<>(user, userRepository.fetchAuthority(user)));
        }

        return userList;
    }

    /**
     * @param searchTerm input by user
     * @return List of users that match search term
     */
    public List<Tuple2<User, Authority>> searchAllUsers(String searchTerm) {

        List<Tuple2<User, Authority>> userList = new ArrayList<>();

        for (User user : userRepository.searchAllUsers(searchTerm)) {
            userList.add(new Tuple2<>(user, userRepository.fetchAuthority(user)));
        }

        return userList;
    }

    // connects to repository
    public String checkPrivilege(String username) {
        return userRepository.getAuthority(username);
    }

    /**
     * @param user object
     * @return boolean depending on if user has empty email and name fields
     */
    private boolean hasEmptyFields(User user) {
        if (user.getEmail().isBlank())     return true;
        if (user.getFirstname().isBlank()) return true;
        if (user.getLastname().isBlank())  return true;
        else                               return false;
    }

    /**
     * @param user object
     * @return true if user has empty password field
     */
    private boolean hasEmptyPassword(User user) {
        return user.getPassword().isBlank();
    }

    /**
     * @param user object
     * @return true if user's email fails validation
     */
    public boolean hasInvalidEmail(User user) {
        EmailValidator validator = EmailValidator.getInstance();
        return !validator.isValid(user.getEmail());
    }

    /**
     * @param user object
     * @return true if user's phone number fails validation
     */
    private boolean hasInvalidPhoneNo(User user) {

        try {
            Integer.parseInt(user.getPhone_no());
        } catch (Exception e) {
            return true;
        }
        return user.getPhone_no().length() != 8;
    }

    /**
     * @param user object
     * @return true if user's CPR number fails validation
     */
    private boolean hasInvalidCPR(User user) {

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
