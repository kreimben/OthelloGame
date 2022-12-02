package Server.Response;

import Server.Person.Person;

public class EnterResponse extends GeneralResponse {
    // 204 방에 입장 response. s -> c
    private String roomName;
    private String userName;
    public String usernameList;
    public EnterResponse(String userName, String roomName, String roomSize, String usernameList) {
        super(204, null, roomSize);
        this.roomName = roomName;
        this.userName = userName;
        this.usernameList = usernameList;
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
