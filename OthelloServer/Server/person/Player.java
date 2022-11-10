package Server.person;

import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.PC;
import Server.ProtocolNumber;
import Server.Request.GameRequest;
import Server.Response.GameResponse;
import Server.Response.GeneralResponse;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

// 2명 이하일때 생성되는 객체로써 직접적으로 클라이언트로부터 request를 받을 수 있습니다.
public class Player extends Person {

    public Player(Socket clientSocket, RoomManager rm) {
        super(clientSocket, rm);
    }

    // 플레이어로부터 듣습니다. http서버에서 사용되는 response와 같습니다.
    public void listen() {
        try {
            var req = super.internetStream.receive();
            OthelloServer.getInstance().printTextToServer(req.message.get());
            OthelloServer.getInstance().printTextToServer("New message from client" + req.person + "\n" + "message: " + req.message);

            switch (req.code) {
                case 100:
                    // 100 좌표를 이용해 플레이 함 request. c -> s
                    OthelloServer.getInstance().printTextToServer(
                            req.person.getUserName() + " played to x: " + ((GameRequest) req).getX()
                                    + " / y: " + ((GameRequest) req).getY()
                    );
                    rm.broadcast(
                            new GameResponse( // 102 좌표를 이용해 플레이함 response. s -> c
                                    this,
                                    Optional.empty(),
                                    ((GameRequest) req).getX(),
                                    ((GameRequest) req).getY()
                            )
                    );
                    break;
                case 202:
                    // 202 방에 입장 request. c -> s
                    rm.broadcast(
                            new GeneralResponse(
                                    PC.getInstance().convert(ProtocolNumber.ENTERED_ROOM_204), // 204 방에 입장 response. s -> c
                                    this,
                                    Optional.empty()
                            )
                    );
                    break;
                case 203:
                    rm.boomRoom(super.roomName);
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException | IOException e) {
            OthelloServer.getInstance().printTextToServer(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            this.listen();
        }
    }
}