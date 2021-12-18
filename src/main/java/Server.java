import UtilClasses.ClientDetails;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server extends Application {
    private static List<TaskClientConnection> connectionList = new ArrayList<TaskClientConnection>();
    public HashMap<String, ClientDetails> onlineClients=new HashMap<String,ClientDetails>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Server Started");
        try{
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(ConnectionUtil.port);

            //continuous loop
            while(true){
                // Listen for a connection request, add new connection to the list
                Socket socket = serverSocket.accept();
                TaskClientConnection connection = new TaskClientConnection(socket, this);
                connectionList.add(connection);

                //create a new thread
                Thread thread = new Thread((Runnable) connection);
                thread.start();
            }
        }
        catch (Exception e) { }
    }
}