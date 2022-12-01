package Server.Person;

import Server.Exceptions.GameOverException;
import Server.Exceptions.PlayerOutException;
import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.PC;
import Server.ProtocolNumber;
import Server.Request.GameRequest;
import Server.Request.GeneralRequest;
import Server.Response.EnterResponse;
import Server.Response.GameResponse;
import Server.Response.GeneralResponse;
import Server.Response.HistoryResponse;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;

// 2명 이하일때 생성되는 객체로써 직접적으로 클라이언트로부터 request를 받을 수 있습니다.
public class Player extends Person implements Serializable {

    public Player(Socket clientSocket, RoomManager rm) {
        super(clientSocket, rm);
    }

    // 플레이어로부터 듣습니다. http서버에서 사용되는 response와 같습니다.
    public void listen() throws GameOverException, PlayerOutException {
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
                    String coordinate = ((GameRequest) req).getX() + "," + ((GameRequest) req).getY() + "," + ":";
                    super.writeHistory(super.roomName, coordinate);

                    // 서버 화면에 적음.
                    OthelloServer.getInstance().printTextToServer(req.message + " played to x: " + ((GameRequest) req).getX() + " / y: " + ((GameRequest) req).getY());

                    // 접속한 모든 client들에게 모두 전파함.
                    int x = ((GameRequest) req).getX();
                    int y = ((GameRequest) req).getY();
                    rm.broadcastOthers(new GameResponse( // 102 좌표를 이용해 플레이함 response. s -> c
                            null, super.userName, x, y), super.userName);
                }
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
                case 202 -> {
                    // 입장할 때 클라이언트가 플레이어인지, 옵저버인지 `instanceof`로 체크하세요.
                    // 만약 `player instanceof Player`를 이용한다면 플레이어인지, 옵저버인지 체크 할 수 있습니다.
                    // 202 방에 입장 request. c -> s
                    String roomSize = Integer.toString(RoomManager.getRooms().get(roomName).size()); //방에 몇명이 있는지
                    rm.broadcast(new EnterResponse(userName, roomName, roomSize)); // 204 방에 입장 response. s -> c
                }
                case 203 -> { // 접속 종료
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
                    rm.broadcast(someoneDisconnectResponse); //모든 유저에게 전송

                    //4. 출력
                    OthelloServer.getInstance().printTextToServer(req.message + "님이 접속 종료 하셨습니다.");

                    //5. 닫기
                    super.close();
                }
                case 400 -> {
                    OthelloServer.getInstance().printTextToServer("Game Start");
                    int code = PC.getInstance().convert(ProtocolNumber.GameStart_401);
                    GeneralResponse gameStartResponse = new GeneralResponse(code, null, "Game Start");
                    rm.broadcast(gameStartResponse);
                }
                case 402 -> {
                    OthelloServer.getInstance().printTextToServer(req.message);
                    int code = PC.getInstance().convert(ProtocolNumber.GameEnd_403);
                    GeneralResponse gameEndResponse = new GeneralResponse(code, null, req.message);
                    rm.broadcast(gameEndResponse);
                }
                case 501 -> {
                    rm.broadcast(
                            new GeneralResponse(
                                    PC.getInstance().convert(ProtocolNumber.READY_RESPONSE_502), // 101 General Response
                                    null,
                                    req.message
                            )
                    );
                }
                case 601 -> {
                    rm.broadcast(
                            new GeneralResponse(
                                    PC.getInstance().convert(ProtocolNumber.PLAYER_GIVEUP_RES_602), // 101 General Response
                                    null,
                                    req.message
                            )
                    );
                }
                default ->
                        OthelloServer.getInstance().printTextToServer("Client's Unhandled Request Code: " + req.code);
            }
        } catch (SocketException e) {
            OthelloServer.getInstance().printTextToServer(e.getMessage());
            throw new PlayerOutException(e.getMessage());
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
            } catch (PlayerOutException e) {
                break;
            }
        }
    }
}