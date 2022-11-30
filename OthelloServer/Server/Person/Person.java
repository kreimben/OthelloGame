package Server.Person;

import Server.Exceptions.GameOverException;
import Server.Exceptions.PlayerOutException;
import Server.InternetStream;
import Server.Manager.RoomManager;
import Server.OthelloServer;
import Server.Request.GameRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

// 네트워크 통신 중 서버에 접속한 모든 클라이언트를 뜻합니다.
// 네트워크 대상자가 모두 사람일 수 밖에 없어서 이렇게 작명 했습니다.
public abstract class Person extends Thread implements Serializable {
    protected String roomName;
    protected InternetStream internetStream = null; // 스트림 객체를 이용해 편리하게 소통하기 위함입니다.
    protected ObjectOutputStream oos;
    protected ObjectInputStream ois;
    protected Socket socket; // 클라이언트 소켓이 저장됩니다.
    protected String userName = ""; // 유저 이름을 저장합니다.
    protected RoomManager rm;

    protected boolean isConnected = true;

    Person(Socket socket, RoomManager rm) {
        try {
            this.socket = socket;
            // 입력 받은 소켓을 바탕으로 통신을 더 편하게 하기 위해 별도의 스트림 객체를 초기화시켜 줍니다.
            //this.internetStream = new InternetStream(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            OthelloServer.getInstance().printTextToServer("InternetStream initialization error");
        }
        this.rm = rm;
    }

    public void close()
    {
        try{
            isConnected = false;
            oos.close();
            ois.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //인풋 스트림 받음
    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    //아웃풋 스트림 받음
    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
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
            //this.internetStream.send(res);
            this.oos.writeObject(res);
        } catch (IOException e) {
            OthelloServer.getInstance().printTextToServer("\n통신 에러...");
            OthelloServer.getInstance().printTextToServer(e.getMessage());
        }
    }

    // 플레이어로부터 듣습니다. http서버에서 사용되는 response와 같습니다.
    public abstract void listen() throws GameOverException, PlayerOutException;

    // 방 이름으로 저장된 대국 정보들을 반환 합니다.
    protected String getHistory(String roomName) {
        return RoomManager.history.get(roomName);
    }

    // 방 이름으로 대국 정보를 기록합니다.
    protected void writeHistory(String roomName, String coordinate) {
        String history = RoomManager.history.get(roomName);
        history += coordinate;
        RoomManager.history.put(roomName, history);
    }
}
