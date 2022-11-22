package Server.Person;

import Server.Exceptions.GameOverException;
import Server.InternetStream;
import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.Request.GameRequest;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

// 네트워크 통신 중 서버에 접속한 모든 클라이언트를 뜻합니다.
// 네트워크 대상자가 모두 사람일 수 밖에 없어서 이렇게 작명 했습니다.
public abstract class Person extends Thread implements Serializable {
    public String roomName;
    protected InternetStream internetStream = null; // 스트림 객체를 이용해 편리하게 소통하기 위함입니다.
    protected Socket socket; // 클라이언트 소켓이 저장됩니다.
    protected String userName = ""; // 유저 이름을 저장합니다.
    protected RoomManager rm;

    Person(Socket socket, RoomManager rm) {
        try {
            // 입력 받은 소켓을 바탕으로 통신을 더 편하게 하기 위해 별도의 스트림 객체를 초기화시켜 줍니다.
            this.internetStream = new InternetStream(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            OthelloServer.getInstance().printTextToServer("InternetStream initialization error");
        }
        this.rm = rm;
    }

    // 소켓을 받습니다.
    public Socket getSocket() {
        return this.socket;
    }

    // 플레이어 이름을 받습니다.
    public String getUserName() {
        return this.userName;
    }

    // 플레이어 이름을 저장해 줍니다.
    public void setUserName(String userName) {
        this.userName = userName;
    }

    // 플레이어에게 말합니다. http서버에서 사용되는 response와 같습니다.
    public void say(Object res) {
        try {
            this.internetStream.send(res);
        } catch (IOException e) {
            OthelloServer.getInstance().printTextToServer("\n통신 에러...");
            OthelloServer.getInstance().printTextToServer(e.getMessage());
        }
    }

    // 플레이어로부터 듣습니다. http서버에서 사용되는 response와 같습니다.
    public abstract void listen() throws GameOverException;

    // 방 이름으로 저장된 대국 정보들을 반환 합니다.
    protected ArrayList<GameRequest> getHistory(String roomName) {
        return RoomManager.history.get(roomName);
    }

    // 방 이름으로 대국 정보를 기록합니다.
    protected void writeHistory(String roomName, GameRequest req) {
        RoomManager.history.get(roomName).add(req);
    }
}
