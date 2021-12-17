import UtilClasses.LoginInfo;
import UtilClasses.SignupInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
public class TaskClientConnection implements Runnable{
    Socket socket;
    Server server;

    public TaskClientConnection(Socket socket, Server server) {
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
                        boolean b1 = oi.readBoolean();
                        System.out.println(loginInfo.getUsername() + " " + loginInfo.getPassword());
                        System.out.println(b1);
                        boolean b = true;
                        os.writeBoolean(b);
                        os.flush();
                        break;
                    case 3:
                        SignupInfo signupInfo = (SignupInfo) oi.readObject();
                        System.out.println(signupInfo.getName() + " " + signupInfo.getUsername() + " " + signupInfo.getEmail() + " " + signupInfo.getPassword());
                        os.writeBoolean(true);
                        os.flush();
                        break;
                    default:
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {}
        }
    }
}
