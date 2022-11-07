package Server.person;

import java.net.Socket;

// 2명 이상이 같은 방을 들어갔을 때 발생되는 클래스입니다. (관전자)
public class Observer extends Person {
    public Observer(Socket clientSocket) {
        super(clientSocket);
    }
}
