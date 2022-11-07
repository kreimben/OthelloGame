package Server.person;

import java.net.Socket;

public class Observer extends Person {
    public Observer(Socket clientSocket) {
        super(clientSocket);
    }
}
