package com.healthink.user.healthink;

/**
 * Created by Ahsanul Marom on 11/07/2017.
 */

public class UserData {
    private String displayName;
    private String bioUser;
    private String username;
    private int pict;

    public UserData() {

    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getBioUser() {
        return bioUser;
    }

    public void setBioUser(String bioUser) {
        this.bioUser = bioUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPict() {
        return pict;
    }

    public void setPict(int pict) {
        this.pict = pict;
    }
}
