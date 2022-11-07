package Server.person;

import java.net.Socket;

public class Player extends Person {

  /** 생성자 */
  public Player(Socket clientSocket) {
    super(clientSocket);
  }
}
