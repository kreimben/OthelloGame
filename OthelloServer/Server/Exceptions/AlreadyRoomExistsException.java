package Server.Exceptions;

// 똑같은 이름의 방이 이미 있을 때 raise되는 exception입니다.
public class AlreadyRoomExistsException extends Exception {
    public AlreadyRoomExistsException(String msg) {
        super(msg);
    }
}
