package com.optimal.loyalty.Optimal.Loyalty.response;


public class UserDetailsResponse {
    private String username;
    private String userEmail;

//TODO: Change it later 
    public UserDetailsResponse(String username , String userEmail) {
        this.username = username;
        this.userEmail = userEmail;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
