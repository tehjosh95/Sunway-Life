package com.example.android.myfyp;

public class UserProfile {
    public String userAge;
    public String userEmail;
    public String userName;

    public String userType;

    public UserProfile(){
    }

    public UserProfile(String userAge, String userEmail, String userName, String userType) {
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userType = userType;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() { return userType; }

    public void setUserType(String userType) { this.userType = userType; }
}
