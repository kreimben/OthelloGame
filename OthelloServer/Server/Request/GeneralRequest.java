package Server.Request;

import Server.person.Person;

import java.io.Serializable;
import java.util.Optional;

// 일반적으로 사용되는 Request객체 입니다.
public class GeneralRequest implements Serializable {
    /**
     * 100 좌포를 이용해 플레이 함 request. c -> s
     * 101 서버로부터 메세지를 받음 response. s -> c
     * 200 방을 만듬 request. c -> s
     * 201 방이 만들어짐 response. s -> c
     * 202 방에 입장 request. c -> s
     * 203 접속 종료 request. c -> s
     */
    public int code; // 위에 있는 프로토콜 코드입니다.
    public Person person; // 대상 클라이언트 입니다.
    public Optional<String> message; // 추가적으로 받은 메세지입니다.

    public GeneralRequest(int code, Person person, Optional<String> message) {
        this.code = code;
        this.person = person;
        this.message = message;
    }
}
