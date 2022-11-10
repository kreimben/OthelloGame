package Server.Request;

import Server.person.Person;

import java.util.Optional;

public class GameRequest extends GeneralRequest {
    /*
     * 100 좌표를 이용해 플레이 함 request. c -> s
     */
    private final int x;
    private final int y;

    public GameRequest(Person person, Optional<String> message, int x, int y) {
        super(100, person, message);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}