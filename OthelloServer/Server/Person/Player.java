package Server.person;

import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.PC;
import Server.ProtocolNumber;
import Server.Request.GameRequest;
import Server.Response.GameResponse;
import Server.Response.GeneralResponse;
import Server.Response.HistoryResponse;

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
                    // 대국 기록을 저장함.
                    super.writeHistory(super.roomName, (GameRequest) req);

                    // 서버 화면에 적음.
                    OthelloServer.getInstance().printTextToServer(
                            req.person.getUserName() + " played to x: " + ((GameRequest) req).getX()
                                    + " / y: " + ((GameRequest) req).getY()
                    );

                    // 접속한 모든 client들에게 모두 전파함.
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
                                // 204 방에 입장 response. s -> c
                                new EnterResponse(this, ((EnterRequest) req).getRoomName(), ((EnterRequest) req).getUserName()));
                case 203 -> {
                    // 접속 종료
                    rm.disconnectAllClient(super.roomName);
                    break;
                case 301:
                    // 방 이름의 대국 기록. history.
                    var history = super.getHistory(super.roomName);
                    rm.broadcast(
                            new HistoryResponse(
                                    this,
                                    history
                            )
                    );
                    break;
                default:
                    OthelloServer.getInstance().printTextToServer("Client's Unhandled Request Code: " + req.code);
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