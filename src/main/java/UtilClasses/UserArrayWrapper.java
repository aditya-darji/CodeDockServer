package UtilClasses;

import javafx.collections.ObservableList;

import java.io.Serializable;

public class UserArrayWrapper implements Serializable {
    ObservableList<String> usersList;

    public void setUsersList(ObservableList<String> usersList) {
        this.usersList = usersList;
    }

    public ObservableList<String> getUsersList() {
        return usersList;
    }
}
