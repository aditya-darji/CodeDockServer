package UtilClasses;

import java.io.Serializable;

public class UserAccessInfo implements Serializable {
    private int userId, access;
    private String username;

    public UserAccessInfo(int userId, String username, int access){
        this.userId = userId;
        this.username = username;
        this.access = access;
    }

    public int getAccess() {
        return access;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
