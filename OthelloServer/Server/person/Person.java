package Server.person;

import Server.InternetStream;
import Server.OthelloServer;
import Server.Response.GeneralResponse;

import java.io.IOException;
import java.net.Socket;

// 네트워크 통신 중 서버에 접속한 모든 클라이언트를 뜻합니다.
// 네트워크 대상자가 모두 사람일 수 밖에 없어서 이렇게 작명 했습니다.
public abstract class Person extends Thread {
    private String userName = ""; // 유저 이름을 저장합니다.
    private InternetStream internetStream = null; // 스트림 객체를 이용해 편리하게 소통하기 위함입니다.
    private Socket socket; // 클라이언트 소켓이 저장됩니다.

    Person(Socket socket) {
        try {
            // 입력 받은 소켓을 바탕으로 통신을 더 편하게 하기 위해 별도의 스트림 객체를 초기화시켜 줍니다.
            this.internetStream = new InternetStream(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            OthelloServer.getInstance().printTextToServer("InternetStream initialization error");
        }
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
    public void say(GeneralResponse msg) {
        try {
            this.internetStream.send(msg);
        } catch (IOException e) {
            OthelloServer.getInstance().printTextToServer("\n통신 에러...");
            OthelloServer.getInstance().printTextToServer(e.getMessage());
        }
    }
}
