package Server.Person;

import Server.Exceptions.GameOverException;
import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.PC;
import Server.ProtocolNumber;
import Server.PC;
import Server.ProtocolNumber;
import Server.Request.GameRequest;
import Server.Request.GeneralRequest;
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
            GeneralRequest req = (GeneralRequest) super.ois.readObject();
            OthelloServer.getInstance().printTextToServer(req.message);
            OthelloServer.getInstance().printTextToServer("New message from client" + req.person + "\n" + "message: " + req.message);

            switch (req.code) {
                case 104 -> {
                    // 104 채팅 request. c -> s
                    rm.broadcast(
                            new GeneralResponse(
                                    PC.getInstance().convert(ProtocolNumber.RESPONSE_101), // 101 General Response
                                    null,
                                    req.message
                            )
                    );
                }
                case 202 ->
                    // 입장할 때 클라이언트가 플레이어인지, 옵저버인지 `instanceof`로 체크하세요.
                    // 만약 `player instanceof Player`를 이용한다면 플레이어인지, 옵저버인지 체크 할 수 있습니다.
                    // 202 방에 입장 request. c -> s
                        rm.broadcast(
                                // 204 방에 입장 response. s -> c
                                new EnterResponse(userName, roomName, Integer.toString(RoomManager.getRooms().get(roomName).size())));
                case 203 -> {
                    // 접속 종료
                    var list = RoomManager.getRooms().get(roomName);
                    list.remove(this);
                    RoomManager.getRooms().put(roomName, list);

                    OthelloServer.getInstance().printTextToServer(req.message + "님이 접속 종료 하셨습니다.");
                    var clist = rm.getClientList();
                    clist.remove(this);

                    int code = PC.getInstance().convert(ProtocolNumber.RESPONSE_101);
                    GeneralResponse someoneQuitResponse = new GeneralResponse(code, null, req.message + "님이 접속 종료 하셨습니다.");
                    rm.broadcast(someoneQuitResponse);
                    super.close();

                    //rm.disconnectAllClient(super.roomName);
                    //throw new GameOverException(req.person.userName + "가 게임을 종료 하였습니다.");
                }
                case 301 -> {
                    // 방 이름의 대국 기록. history.
                    var history = super.getHistory(super.roomName);
                    oos.writeObject(new HistoryResponse(null, history));
                }
                default ->
                        OthelloServer.getInstance().printTextToServer("Client's Unhandled Request Code: " + req.code);
            }
        } catch (ClassNotFoundException | IOException e) {
            OthelloServer.getInstance().printTextToServer(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //super.run();

        while (isConnected) {
            this.listen();
        }
    }
}
