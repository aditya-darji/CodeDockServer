package UtilClasses;

import java.net.Socket;

public class ClientDetails {
    private Socket socket;
    private int userId;
    private String username, name, email;

    public ClientDetails(Socket socket, UseridInfo useridInfo){
        this.socket = socket;
        this.userId = useridInfo.getUserid();
        this.username = useridInfo.getUsername();
        this.name = useridInfo.getName();
        this.email = useridInfo.getEmail();
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getUserId() {
        return userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

