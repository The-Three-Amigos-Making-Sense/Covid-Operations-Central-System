package com.thethreeamigosmakingsense.covidoperationscentralsystem.Model;

public class Authority {

    int CPR;
    String authority;

    public Authority(User user, String authority) {
        CPR = user.getCPR();
        this.authority = authority;
    }

    public int getUserCPR() {
        return CPR;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
