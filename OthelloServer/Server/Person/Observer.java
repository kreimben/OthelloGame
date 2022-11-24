package Server.Person;

import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.PC;
import Server.ProtocolNumber;
import Server.Request.EnterRequest;
import Server.Request.GameRequest;
import Server.Response.EnterResponse;
import Server.Response.GeneralResponse;
import Server.Response.HistoryResponse;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

// 2명 이상이 같은 방을 들어갔을 때 발생되는 클래스입니다. (관전자)
public class Observer extends Person implements Serializable {

    public Observer(Socket clientSocket, RoomManager rm) {
        super(clientSocket, rm);
    }

    @Override
    public void listen() {
        try {
            var req = super.internetStream.receive();
            OthelloServer.getInstance().printTextToServer(req.message);
            OthelloServer.getInstance().printTextToServer("New message from client" + req.person + "\n" + "message: " + req.message);

            switch (req.code) {
                case 100 ->
                    // 100 좌표를 이용해 플레이 함 request. c -> s
                        OthelloServer.getInstance().printTextToServer(req.person.getUserName() + " played to x: " + ((GameRequest) req).getX() + "y: " + ((GameRequest) req).getY());
                case 104 -> {
                    // 104 채팅 request. c -> s
                    rm.broadcast(
                            new GeneralResponse(
                                    PC.getInstance().convert(ProtocolNumber.RESPONSE_101), // 101 General Response
                                    this,
                                    ""
                            )
                    );
                }
                case 202 ->
                    // 202 방에 입장 request. c -> s
                        rm.broadcast(
                                // 204 방에 입장 response. s -> c
                                new EnterResponse(userName, roomName, Integer.toString(RoomManager.getRooms().get(roomName).size())));
                case 301 -> {
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
