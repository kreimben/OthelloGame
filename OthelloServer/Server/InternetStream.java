package Server;

import Server.Request.GeneralRequest;

import java.io.*;

// 서버와 클라이언트 통신을 통일 시키기 위해 이 객체를 이용해 Response와 Request를 실행합니다.
public class InternetStream implements Serializable{

    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;

    public InternetStream(InputStream is, OutputStream os) {
        try {
            this.is = new ObjectInputStream(is);
            this.os = new ObjectOutputStream(os);
            this.os.flush();
        } catch (Exception e) {
            OthelloServer.getInstance().printTextToServer("Cannot make OthelloServer.InternetStream");
            e.printStackTrace();
        }
    }

    // 대상 클라이언트에게 response객체를 보냅니다.
    public void send(Object res) throws IOException {
        os.writeObject(res);
    }

    // 대상 클라이언트로부터 request를 받습니다.
    public GeneralRequest receive() throws IOException, ClassNotFoundException {
        return (GeneralRequest) is.readObject();
    }
}
