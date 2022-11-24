package Server.Request;

import Server.Person.Person;

public class EnterRequest extends GeneralRequest {
    // 202 방에 입장 request. c -> s
    private String userName;
    private String roomName;

    public EnterRequest(Person person, String roomName, String userName) {
        super(202, person, "");
        this.userName = userName;
        this.roomName = roomName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
