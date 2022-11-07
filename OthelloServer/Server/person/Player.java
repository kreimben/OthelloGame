package Server.person;

import java.net.Socket;

// 2명 이하일때 생성되는 객체로써 직접적으로 클라이언트로부터 request를 받을 수 있습니다.
public class Player extends Person {
    public Player(Socket clientSocket) {
        super(clientSocket);
    }
}