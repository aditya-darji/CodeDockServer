import UtilClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class TaskClientConnection implements Runnable{
    Socket socket;
    Server server;
    Conn conn = new Conn();
    ClientDetails clientDetails = null;

    public TaskClientConnection(Socket socket, Server server) throws SQLException, ClassNotFoundException {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println(this.socket);
        while(socket != null){
            try {
                ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                int choice = (int) oi.readInt();

                switch (choice){
                    case 1:
                        String message = oi.readUTF();
                        os.writeUTF("Hello From Server");
                        os.flush();
                        System.out.println(message);
                        break;
                    case 2:
                        LoginInfo loginInfo = (LoginInfo) oi.readObject();
                        UseridInfo useridInfo = LoginEncrypted(loginInfo);
                        clientDetails = new ClientDetails(socket, useridInfo);
                        server.onlineClients.put(useridInfo.getUsername(), clientDetails);
                        os.writeObject(useridInfo);
                        os.flush();
                        break;
                    case 3:
                        SignupInfo signupInfo = (SignupInfo) oi.readObject();
                        int b = SignupEncrypted(signupInfo);
                        os.writeInt(b);
                        os.flush();
                        break;
                    case 4:
                        ArrayList<String> usersList = getAllUsers();
                        os.writeObject(usersList);
                        os.flush();
                        break;
                    case 5:
                        NewDocumentInfo newDocumentInfo = (NewDocumentInfo) oi.readObject();
                        createNewRoom(newDocumentInfo);
                        os.writeUTF("ROOM-CREATED");
                        os.flush();
                        break;
                    case 6:
                        String sendTo = oi.readUTF();
                        String messageToSend = oi.readUTF();
                        if(sendTo.equals("STOP-THREAD")){
                            os.writeUTF(sendTo);
                        }
                        else{
                            if(server.onlineClients.containsKey(sendTo)){
                                ClientDetails sendToClient = server.onlineClients.get(sendTo);
                                ObjectOutputStream sendToOO = new ObjectOutputStream(sendToClient.getSocket().getOutputStream());
                                sendToOO.writeUTF("[" + clientDetails.getUsername() + "]: " + messageToSend + "");
                                sendToOO.flush();
                            }
                            os.writeUTF("SERVER-REPLY");
                        }
                        os.flush();
                        break;
                    case 7:
                        ArrayList<DocumentDetails> documentsList = getAllDocuments();
                        os.writeObject(documentsList);
                        os.flush();
                        break;
                    case 8:
                        String personalSendTo = oi.readUTF();
                        String personalMessageToSend = oi.readUTF();
                        os.writeInt(2);
                        if(server.onlineClients.containsKey(personalSendTo)){
                            ClientDetails personalSendToClient = server.onlineClients.get(personalSendTo);
                            ObjectOutputStream personalSendToOO = new ObjectOutputStream(personalSendToClient.getSocket().getOutputStream());
                            personalSendToOO.writeInt(1);
                            personalSendToOO.writeUTF("[" + clientDetails.getUsername() + "]: " + personalMessageToSend + "");
                            personalSendToOO.flush();
                            os.writeUTF("MESSAGE_SENT");
                        }
                        else{
                            os.writeUTF("Message not sent as receiver is offline.");
                        }
                        os.flush();
                        break;
                    case 9:
                        os.writeInt(1000);
                        os.flush();
                        break;
                    case 10:
                        int chatRoomId = oi.readInt();
                        String roomMessage = oi.readUTF();
                        os.writeInt(2);
                        if(sendMessageInRoom(chatRoomId, roomMessage)){
                            os.writeUTF("MESSAGE_SENT");
                        }
                        else{
                            os.writeUTF("MESSAGE_NOT_SENT");
                        }
                        os.flush();
                        break;
                    case 11:
                        int roomId1 = oi.readInt();
                        ArrayList<UserAccessInfo> userAccessInfoArrayList = getRoomDetails(roomId1);
                        ArrayList<String> usersList1 = getAllUsers();
                        os.writeInt(4);
                        os.writeObject(userAccessInfoArrayList);
                        os.writeObject(usersList1);
                        os.flush();
                        break;
                    case 12:
                        int roomId2 = oi.readInt();
                        ArrayList<UserAccessInfo> userAccessInfoArrayList1 = (ArrayList<UserAccessInfo>) oi.readObject();
                        updateUserAccess(roomId2, userAccessInfoArrayList1);
                        os.writeInt(2);
                        os.writeUTF("ACCESS_UPDATED");
                        os.flush();
                        break;
                    case 13:
                        ArrayList<String> usersList2 = getAllUsers();
                        os.writeInt(5);
                        os.writeObject(usersList2);
                        os.flush();
                        break;
                    default:
                        String s = oi.readUTF();
                        System.out.println(socket);
                        System.out.println(s);
                        break;
                }

            } catch (IOException | ClassNotFoundException | SQLException e) { e.printStackTrace();}
        }

        server.onlineClients.remove(clientDetails.getUsername());
    }

    private void updateUserAccess(int roomId2, ArrayList<UserAccessInfo> userAccessInfoArrayList1) {
        try{
            PreparedStatement stmt = conn.c.prepareStatement("DELETE FROM room_details WHERE room_id=?");
            stmt.setInt(1, roomId2);
            stmt.executeUpdate();

            PreparedStatement stmt2 = conn.c.prepareStatement("INSERT INTO room_details(room_id, user_id, access) VALUES(?,?,?)");
            stmt2.setInt(1, roomId2);
            stmt2.setInt(2, clientDetails.getUserId());
            stmt2.setInt(3, 0);
            stmt2.addBatch();
            for (UserAccessInfo userAccessInfo : userAccessInfoArrayList1) {
                stmt2.setInt(1, roomId2);
                stmt2.setInt(2, userAccessInfo.getUserId());
                stmt2.setInt(3, userAccessInfo.getAccess());
                stmt2.addBatch();
            }

            int[] updateCounts = stmt2.executeBatch();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private ArrayList<UserAccessInfo> getRoomDetails(int roomId) {
        ArrayList<UserAccessInfo> userAccessInfoArrayList = new ArrayList<UserAccessInfo>();

        try{
            PreparedStatement stmt = conn.c.prepareStatement("SELECT user.user_id user_id, user.user_name user_name, room_details.access access FROM user INNER JOIN room_details ON user.user_id=room_details.user_id WHERE room_details.room_id=?");
            stmt.setInt(1, roomId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                if(rs.getInt(1) == clientDetails.getUserId()) continue;
                UserAccessInfo userAccessInfo = new UserAccessInfo(rs.getInt(1), rs.getString(2), rs.getInt(3));
                userAccessInfoArrayList.add(userAccessInfo);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userAccessInfoArrayList;
    }

    private boolean sendMessageInRoom(int chatRoomId, String roomMessage) {
        try{
            PreparedStatement stmt = conn.c.prepareStatement("SELECT user.user_name AS `user_name` FROM user INNER JOIN room_details ON user.user_id=room_details.user_id WHERE room_details.room_id=?");
            stmt.setInt(1, chatRoomId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String receiverUsername = rs.getString("user_name");
//                System.out.println(receiverUsername);
                if(receiverUsername.equals(clientDetails.getUsername())) continue;
                if(server.onlineClients.containsKey(receiverUsername)){
                    ClientDetails roomSendToClient = server.onlineClients.get(receiverUsername);
                    ObjectOutputStream sendToOO = new ObjectOutputStream(roomSendToClient.getSocket().getOutputStream());
                    sendToOO.writeInt(3);
                    sendToOO.writeUTF("[" + clientDetails.getUsername() + "]: " + roomMessage + "");
                    sendToOO.flush();
                }
            }
            return true;
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private ArrayList<DocumentDetails> getAllDocuments() {
        ArrayList<DocumentDetails> resultArray = new ArrayList<DocumentDetails>();
        try{
            PreparedStatement stmt = conn.c.prepareStatement("SELECT room.room_id AS `room_id`, room.file_name AS `file_name`, room.file_extension AS `file_extension`, room.file_content AS `file_content`, room.created_at AS `created_at`, room.creator_id AS `creator_id`, room_details.access AS `access` FROM `room` INNER JOIN room_details ON room.room_id=room_details.room_id WHERE room_details.user_id=? ORDER BY `created_at` DESC");
            stmt.setInt(1, clientDetails.getUserId());

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
//                System.out.println(rs.getInt("user_id") + "-" + rs.getString("name") + "-" + rs.getString("user_name"));
                DocumentDetails documentDetails = new DocumentDetails(rs.getInt("room_id"), rs.getInt("creator_id"), rs.getInt("access"), rs.getString("file_name"), rs.getString("file_extension"), rs.getString("file_content"), rs.getTimestamp("created_at"));
                resultArray.add(documentDetails);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultArray;
    }

    private void createNewRoom(NewDocumentInfo newDocumentInfo) throws SQLException {
        try{
            PreparedStatement pstmt = conn.c.prepareStatement("SELECT MAX(room_id) AS maxRoom FROM room");
            ResultSet rs0 = pstmt.executeQuery();
            int maxRoomId = 0;
            if(rs0.next()){
                maxRoomId = rs0.getInt("maxRoom");
            }
            int room_id = maxRoomId+1;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            PreparedStatement stmt = conn.c.prepareStatement("INSERT INTO room(`room_id`, `file_name`, `file_extension`, `file_content`, `created_at`, `creator_id`) VALUES(?,?,?,?,?,?)");
            stmt.setInt(1, room_id);
            stmt.setString(2, newDocumentInfo.getDocumentName());
            stmt.setString(3, newDocumentInfo.getDocumentExtension());
            stmt.setString(4, newDocumentInfo.getDocumentContent());
            stmt.setTimestamp(5, timestamp);
            stmt.setInt(6, clientDetails.getUserId());

            stmt.executeUpdate();

            System.out.println(room_id);
            StringBuilder placeholders = new StringBuilder();
            PreparedStatement stmt2 = conn.c.prepareStatement("INSERT INTO room_details(room_id, user_id, access) VALUES(?,?,?)");
            stmt2.setInt(1, room_id);
            stmt2.setInt(2, clientDetails.getUserId());
            stmt2.setInt(3, 0);
            stmt2.addBatch();
            for(Map.Entry<Integer, Integer> mapElement: newDocumentInfo.getCollaboratorMap().entrySet()){
                Integer key = mapElement.getKey();
                Integer value = mapElement.getValue();
                stmt2.setInt(1, room_id);
                stmt2.setInt(2, key);
                stmt2.setInt(3, value);
                stmt2.addBatch();
            }

            int[] updateCounts = stmt2.executeBatch();

//                for(Map.Entry mapElement: newDocumentInfo.getCollaboratorMap().entrySet()){
//                    placeholders.append("(?,?,?),");
////                    Integer key = (Integer) mapElement.getKey();
////                    Integer value = (Integer) mapElement.getValue();
//                }
//                sql.append(placeholders);
//
//                PreparedStatement stmt2 = conn.c.prepareStatement(sql.toString());
//                for(Map.Entry mapElement: newDocumentInfo.getCollaboratorMap().entrySet()){
//                    Integer key = (Integer) mapElement.getKey();
//                    Integer value = (Integer) mapElement.getValue();
//                    stmt2.()
//                }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private ArrayList<String> getAllUsers() {
        ArrayList<String> resultArray = new ArrayList<String>();
        try{
            PreparedStatement stmt = conn.c.prepareStatement("SELECT `user_id`, `name`, `user_name` FROM user");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
//                System.out.println(rs.getInt("user_id") + "-" + rs.getString("name") + "-" + rs.getString("user_name"));
                if(rs.getInt("user_id") == clientDetails.getUserId()) continue;
                resultArray.add(rs.getInt("user_id") + "-" + rs.getString("name") + "-" + rs.getString("user_name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultArray;
    }

    private UseridInfo LoginEncrypted(LoginInfo loginInfo) {
        UseridInfo useridInfo = new UseridInfo(0, "", "", "");
        if(!server.onlineClients.containsKey(loginInfo.getUsername())) {
            try{
                PreparedStatement stmt = conn.c.prepareStatement("SELECT * FROM user WHERE `user_name`=?");
                stmt.setString(1, loginInfo.getUsername());
                ResultSet rs = stmt.executeQuery();

                if(rs.next()){
                    String providedPassword = loginInfo.getPassword();
                    String hash = rs.getString("hash");
                    String salt = rs.getString("salt");

                    boolean passwordMatch = PasswordUtils.verifyUserPassword(providedPassword.toCharArray(), hash, salt);
                    if(passwordMatch){
                        useridInfo.setUserid(rs.getInt("user_id"));
                        useridInfo.setUsername(rs.getString("user_name"));
                        useridInfo.setName(rs.getString("name"));
                        useridInfo.setEmail(rs.getString("email"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return useridInfo;
    }

    private int SignupEncrypted(SignupInfo signupInfo) {
        char[] myPassword = signupInfo.getPassword().toCharArray();

        // Generate Salt. The generated value can be stored in DB.
        String salt = PasswordUtils.getSalt(30);

        // Protect user's password. The generated value can be stored in DB.
        String hash = PasswordUtils.generateSecurePassword(myPassword, salt);
        try{
            PreparedStatement stmt1 = conn.c.prepareStatement("SELECT * FROM user WHERE `user_name`=? OR `email`=?");
            stmt1.setString(1, signupInfo.getUsername());
            stmt1.setString(2, signupInfo.getEmail());
            ResultSet rs = stmt1.executeQuery();
            if(rs.next()) return 2;

            String query = "INSERT INTO user(`name`, `user_name`, `email`, `salt`, `hash`) values(?,?,?,?,?)";
            PreparedStatement stmt = conn.c.prepareStatement(query);
            stmt.setString(1, signupInfo.getName());
            stmt.setString(2, signupInfo.getUsername());
            stmt.setString(3, signupInfo.getEmail());
            stmt.setString(4, salt);
            stmt.setString(5, hash);

            int i = stmt.executeUpdate();
            if(i == 1) return 1;
            else return 0;
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
