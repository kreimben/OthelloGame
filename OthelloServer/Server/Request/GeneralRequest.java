package Server.Request;

import Server.Person.Person;

import java.io.Serializable;

// 일반적으로 사용되는 Request객체 입니다.
public class GeneralRequest implements Serializable {
    /**
     * 100 좌표를 이용해 플레이 함 request. c -> s
     * 101 서버로부터 메세지를 받음 response. s -> c
     * 102 좌표를 이용해 플레이함 response. s -> c
     * 103 게임이 시작 됨 response. s -> c
     * 202 방에 입장 request. c -> s
     * 203 접속 종료 request. c -> s
     * 204 방에 입장 response. s -> c
     * 301 히스토리 요청 request. c -> s
     * 302 히스토리 응답 response. s -> c
     */
    public final int code; // 위에 있는 프로토콜 코드입니다.
    public final Person person; // 대상 클라이언트 입니다.
    public final String message; // 추가적으로 받은 메세지입니다.

    public GeneralRequest(int code, Person person, String message) {
        this.code = code;
        this.person = person;
        this.message = message;
    }
}
