package UtilClasses;

import java.io.Serializable;

public class UseridInfo implements Serializable {
    private int userid;
    private String username, name, email;

    public UseridInfo(int userid, String username, String name, String email){
        this.userid = userid;
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public int getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
