package com.example.facebook_iso;

import android.net.Uri;

public class loginInfo {
    private String userName;
    private String password;
    private String FirstName;
    private String LastName;
    private Boolean New;
    private Uri profilePic;
    loginInfo(String userName,String password){
        this.userName=userName;
        this.password=password;
        this.New = false;
    }

    loginInfo(String FirstName, String LastName, String userName, String password){
        this.FirstName=FirstName;
        this.LastName=LastName;
        this.userName=userName;
        this.password=password;
        this.New = false;
    }

    public Boolean getNew() {
        return New;
    }

    public void setNew(Boolean New) {
        this.New = New;
    }

    public Uri getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Uri profilePic) {
        this.profilePic = profilePic;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
