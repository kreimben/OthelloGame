package Client.Response;

import Server.Person.Person;

public class EnterResponse extends GeneralResponse {
    // 204 방에 입장 response. s -> c
    private String roomName;
    private String userName;

    public EnterResponse(Person person, String roomName, String userName) {
        super(204, person, "");
        this.roomName = roomName;
        this.userName = userName;
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
