import UtilClasses.*;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                        String sendTo = oi.readUTF();
                        String messageToSend = oi.readUTF();
                        if(server.onlineClients.containsKey(sendTo)){
                            ClientDetails sendToClient = server.onlineClients.get(sendTo);
                            ObjectOutputStream sendToOO = new ObjectOutputStream(sendToClient.getSocket().getOutputStream());
                            sendToOO.writeUTF("[" + clientDetails.getUsername() + "]: " + messageToSend + "");
                            sendToOO.flush();
                        }
                        os.writeUTF("SERVER-REPLY");
                        os.flush();
                        break;
                    default:
                        break;
                }

            } catch (IOException | ClassNotFoundException e) { e.printStackTrace();}
        }

        server.onlineClients.remove(clientDetails.getUsername());
    }

    private UseridInfo LoginEncrypted(LoginInfo loginInfo) {
        UseridInfo useridInfo = new UseridInfo(0, "", "", "");
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
