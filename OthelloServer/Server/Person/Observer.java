package Server.Person;

import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.Request.EnterRequest;
import Server.Request.GameRequest;
import Server.Response.EnterResponse;
import Server.Response.HistoryResponse;

import java.io.IOException;
import java.net.Socket;

// 2명 이상이 같은 방을 들어갔을 때 발생되는 클래스입니다. (관전자)
public class Observer extends Person {

    public Observer(Socket clientSocket, RoomManager rm) {
        super(clientSocket, rm);
    }

    @Override
    public void listen() {
        try {
            var req = super.internetStream.receive();
            OthelloServer.getInstance().printTextToServer(req.message.get());
            OthelloServer.getInstance().printTextToServer("New message from client" + req.person + "\n" + "message: " + req.message);

            switch (req.code) {
                case 100 ->
                    // 100 좌표를 이용해 플레이 함 request. c -> s
                    OthelloServer.getInstance().printTextToServer(req.person.getUserName() + " played to x: " + ((GameRequest) req).getX() + "y: " + ((GameRequest) req).getY());
                    break;
                case 202:
                    // 202 방에 입장 request. c -> s -> c
                    rm.broadcast(new GeneralResponse(PC.getInstance().convert(ProtocolNumber.ENTER_ROOM_202), this, Optional.empty()));
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
                }
                default ->
                        OthelloServer.getInstance().printTextToServer("Client's Unhandled Request Code: " + req.code);
            }
        } catch (ClassNotFoundException | IOException e) {
            OthelloServer.getInstance().printTextToServer(e.getMessage());
            e.printStackTrace();
        }
    }
}