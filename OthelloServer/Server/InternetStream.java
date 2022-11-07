package Server;

import Server.Request.GeneralRequest;
import Server.Response.GeneralResponse;

import java.io.*;

public class InternetStream {

    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;

    public InternetStream(InputStream is, OutputStream os) {
        try {
            this.is = new ObjectInputStream(is);
            this.os = new ObjectOutputStream(os);
        } catch (Exception e) {
            OthelloServer.getInstance().printTextToServer("Cannot make OthelloServer.InternetStream");
            e.printStackTrace();
        }
    }

    public void send(GeneralResponse res) throws IOException {
        os.writeObject(res);
    }

    public GeneralRequest receive() throws IOException, ClassNotFoundException {
        return (GeneralRequest) is.readObject();
    }
}
