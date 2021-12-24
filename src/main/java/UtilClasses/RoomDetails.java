package UtilClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomDetails implements Serializable {
    private int roomId;
    public HashMap<Integer, RoomUser> roomUserHashMap;

    public RoomDetails(int roomId){
        this.roomId = roomId;
        roomUserHashMap = new HashMap<Integer, RoomUser>();
    }

    public int getRoomId() {
        return roomId;
    }

    public HashMap<Integer, RoomUser> getRoomUserHashMap() {
        return roomUserHashMap;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setRoomUserHashMap(HashMap<Integer, RoomUser> roomUserHashMap) {
        this.roomUserHashMap = roomUserHashMap;
    }
}
