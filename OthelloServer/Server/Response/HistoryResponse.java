package Server.Response;

import Server.Person.Person;
import Server.Request.GameRequest;

import java.util.ArrayList;
import java.util.Optional;

public class HistoryResponse extends GeneralResponse {
    /**
     * 302 히스토리 응답 response. s -> c
     */

    private final ArrayList<GameRequest> history;

    public HistoryResponse(Person person, ArrayList<GameRequest> history) {
        super(302, person, Optional.empty());
        this.history = history;
    }

    public ArrayList<GameRequest> getHistory() {
        return this.history;
    }
}
