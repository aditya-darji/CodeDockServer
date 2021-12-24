package UtilClasses;

import java.io.Serializable;

public class RoomUser implements Serializable {
    private int userId;
    private String username;
    private int caretPosition=-1;

    public RoomUser(int userId, String username){
        this.userId = userId;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
    }
}
