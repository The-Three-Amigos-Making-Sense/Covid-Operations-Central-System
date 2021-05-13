package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class User {
    private int CPR;
    private String email;
    private String firstName;
    private String lastName;
    private int phoneNo;
    private String password;
    private boolean enabled = true;

    public User(int CPR, String email, String firstName, String lastName, int phoneNo, String password) {
        this.CPR = CPR;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
        this.password = password;
    }
    public User(String firstName, String password) {
        this.firstName = firstName;
        this.password = password;
    }

    public int getCPR() {
        return CPR;
    }

    public void setCpr(int CPR) {
        this.CPR = CPR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(int phoneNo) {
        this.phoneNo = phoneNo;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
