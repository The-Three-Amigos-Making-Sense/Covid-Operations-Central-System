package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;


public class Authority {

    private String username; // actually CPR
    private String authority; // user's role

    public Authority() {}

    public Authority(User user, String authority) {
        username = user.getUsername();
        this.authority = authority;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
