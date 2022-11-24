package Server.Person;

import Server.Exceptions.GameOverException;
import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.PC;
import Server.ProtocolNumber;
import Server.Request.EnterRequest;
import Server.Request.GameRequest;
import Server.Request.GeneralRequest;
import Server.Response.EnterResponse;
import Server.Response.GameResponse;
import Server.Response.GeneralResponse;
import Server.Response.HistoryResponse;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

// 2명 이하일때 생성되는 객체로써 직접적으로 클라이언트로부터 request를 받을 수 있습니다.
public class Player extends Person implements Serializable {

    public Player(Socket clientSocket, RoomManager rm) {
        super(clientSocket, rm);
        System.out.println(this);
    }

    // 플레이어로부터 듣습니다. http서버에서 사용되는 response와 같습니다.
    public void listen() throws GameOverException {
        try {
            if(socket == null) return;
            //var req = super.internetStream.receive();
            var req = (GeneralRequest) super.ois.readObject();
            OthelloServer.getInstance().printTextToServer(req.message);
            OthelloServer.getInstance().printTextToServer("New message from client" + req.person + "\n" + "message: " + req.message);

            switch (req.code) {
                case 100 -> {
                    // 100 좌표를 이용해 플레이 함 request. c -> s
                    // 대국 기록을 저장함.
                    super.writeHistory(super.roomName, (GameRequest) req);

                    // 서버 화면에 적음.
                    OthelloServer.getInstance().printTextToServer(req.person.getUserName() + " played to x: " + ((GameRequest) req).getX() + " / y: " + ((GameRequest) req).getY());

                    // 접속한 모든 client들에게 모두 전파함.
                    rm.broadcast(new GameResponse( // 102 좌표를 이용해 플레이함 response. s -> c
                            this, "", ((GameRequest) req).getX(), ((GameRequest) req).getY()));
                }
                case 202 ->
                    // 입장할 때 클라이언트가 플레이어인지, 옵저버인지 `instanceof`로 체크하세요.
                    // 만약 `player instanceof Player`를 이용한다면 플레이어인지, 옵저버인지 체크 할 수 있습니다.
                    // 202 방에 입장 request. c -> s
                        rm.broadcast(
                                // 204 방에 입장 response. s -> c
                                new EnterResponse(userName, roomName, Integer.toString(RoomManager.rooms.get(roomName).size())));
                case 203 -> {
                    // 접속 종료
                    var list = RoomManager.rooms.get(roomName);
                    list.remove(this);
                    RoomManager.rooms.put(roomName, list);
                    OthelloServer.getInstance().printTextToServer(req.message + "님이 접속 종료 하셨습니다.");
                    var clist = rm.getClientList();
                    clist.remove(this);

                    var list2 = RoomManager.rooms.get(roomName);
                    for(int i=0; i<list2.size(); i++)
                    {
                        System.out.println("User : " + list2.get(i));
                    }
                    System.out.println("----------");
                    var clist2 = rm.getClientList();
                    for(int i=0; i<clist2.size(); i++)
                    {
                        System.out.println("clist User : " + clist2.get(i));
                    }

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
                    rm.broadcast(new HistoryResponse(this, history));
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
            try {
                this.listen();
            } catch (GameOverException e) {
                OthelloServer.getInstance().printTextToServer(e.getMessage());
                break;
            }
        }
    }
}