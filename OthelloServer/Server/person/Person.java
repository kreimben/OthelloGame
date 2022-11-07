package Server.person;

import Server.InternetStream;
import Server.OthelloServer;
import Server.Response.GeneralResponse;

import java.io.IOException;
import java.net.Socket;

public abstract class Person extends Thread {
    private String userName = "";
    private InternetStream internetStream = null;
    private Socket socket;

    public Person(Socket socket) {

        try {
            this.internetStream = new InternetStream(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            OthelloServer.getInstance().printTextToServer("userService error");
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void say(GeneralResponse msg) {
        try {
            this.internetStream.send(msg);
        } catch (IOException e) {
            OthelloServer.getInstance().printTextToServer("\n통신 에러...");
            OthelloServer.getInstance().printTextToServer(e.getMessage());
        }
    }

    public void run() {

    }
}
