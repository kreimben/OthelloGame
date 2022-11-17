package Server.Response;

import Server.person.Person;

import java.util.Optional;

public class GameResponse extends GeneralResponse {
    /**
     * 102 좌표를 이용해 플레이함 response. s -> c
     */
    private final int x;
    private final int y;

    public GameResponse(Person person, Optional<String> message, int x, int y) {
        super(102, person, message);
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