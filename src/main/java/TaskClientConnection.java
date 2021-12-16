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
                ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
                int choice = (int) oi.readInt();

                switch (choice){
                    case 1:
                        String message = oi.readUTF();
                        System.out.println(message);
                        break;
                }

            } catch (IOException e) {}
        }
    }
}
