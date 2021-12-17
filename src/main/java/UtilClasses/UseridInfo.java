package UtilClasses;

import java.io.Serializable;

public class UseridInfo implements Serializable {
    private int userid;
    private String username;

    public UseridInfo(int userid, String username){
        this.userid = userid;
        this.username = username;
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
}
