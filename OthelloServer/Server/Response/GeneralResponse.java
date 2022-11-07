package Server.Response;

import Server.person.Person;
import java.io.Serializable;
import java.util.Optional;

public class GeneralResponse implements Serializable {
  /**
   * 100 좌포를 이용해 플레이 함 request. c -> s 101 서버로부터 메세지를 받음 response. s -> c 200 방을 만듬 request. c -> s
   * 201 방이 만들어짐 response. s -> c 202 방에 입장 request. c -> s 203 접속 종료 request. c -> s
   */
  public int code;

  public Person person;
  public Optional<String> message;

  public GeneralResponse(int code, Person person, Optional<String> message) {
    this.code = code;
    this.person = person;
    this.message = message;
  }
}
