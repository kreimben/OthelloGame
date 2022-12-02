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
                            ), roomName
                    );
                }
                case 202 -> {
                    // 입장할 때 클라이언트가 플레이어인지, 옵저버인지 `instanceof`로 체크하세요.
                    // 만약 `player instanceof Player`를 이용한다면 플레이어인지, 옵저버인지 체크 할 수 있습니다.
                    // 202 방에 입장 request. c -> s
                    var roomInfo = RoomManager.getRooms().get(roomName);
                    String roomSize = Integer.toString(roomInfo.size()); //방에 몇명이 있는지
                    String usernameList = "";
                    for(int i=0; i<roomInfo.size(); i++)
                    {
                        if(i == roomInfo.size() -1)
                        {
                            usernameList += roomInfo.get(i).getUserName();
                        }
                        else
                            usernameList += (roomInfo.get(i).getUserName() + ",");
                    }
                    rm.broadcast(new EnterResponse(userName, roomName, roomSize, usernameList), roomName); // 204 방에 입장 response. s -> c
                }
                case 203 -> {
                    //1. 룸 유저 리스트에서 해당 유저 제거
                    var list = RoomManager.getRooms().get(roomName);
                    list.remove(this);
                    RoomManager.getRooms().put(roomName, list);

                    //2. 전체 클라이언트 리스트에서 해당 유저 제거
                    var clist = rm.getClientList();
                    clist.remove(this);

                    //3. 통신
                    int code = PC.getInstance().convert(ProtocolNumber.QUIT_CONNECT_205);
                    GeneralResponse someoneDisconnectResponse = new GeneralResponse(code, null, req.message); //req.message는 접속종료한 유저의 이름
                    rm.broadcast(someoneDisconnectResponse, roomName); //모든 유저에게 전송

                    //4. 출력
                    OthelloServer.getInstance().printTextToServer(req.message + "님이 접속 종료 하셨습니다.");

                    //5. 닫기
                    super.close();
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
