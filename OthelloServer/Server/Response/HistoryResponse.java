package Server.Response;

import Server.Person.Person;
import Server.Request.GameRequest;

import java.util.ArrayList;
import java.util.Optional;

public class HistoryResponse extends GeneralResponse {
    /**
     * 302 히스토리 응답 response. s -> c
     */

    private final String history;

    public HistoryResponse(Person person, String history) {
        super(302, person, "");
        this.history = history;
    }

    public String getHistory() {
        return this.history;
    }
}
