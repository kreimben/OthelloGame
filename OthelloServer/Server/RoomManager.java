package Server;

import Server.Exceptions.AlreadyRoomExistsException;
import Server.Response.GeneralResponse;
import Server.person.Observer;
import Server.person.Person;
import Server.person.Player;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class RoomManager extends Thread {

  private static final int BUF_LEN = 128;
  public static Map<String, ArrayList<Person>> rooms;
  public static ServerSocket serverSocket;
  private final ArrayList<Person> clientList = new ArrayList();

  RoomManager(int port) {
    try {
      RoomManager.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }
    RoomManager.rooms = new HashMap<>();
  }

  public static void makeNewRoom(String roomName) throws AlreadyRoomExistsException {
    if (RoomManager.rooms.get(roomName) != null) {
      throw new AlreadyRoomExistsException("Already this room exists.");
    } else {
      RoomManager.rooms.put(roomName, new ArrayList<>());
    }
    RoomManager.rooms.forEach(
        (key, value) -> System.out.println("key: " + key + " / " + "value" + value));
  }

  @Override
  public void run() {
    /*
     * 그냥 계속해서 접속자를 받음.
     */
    while (true) {
      try {
        OthelloServer.getInstance().printTextToServer("새로운 참가자를 기다리는 중...\n");
        var socket = serverSocket.accept();
        OthelloServer.getInstance().printTextToServer("새로운 참가자 from " + socket);

        Person person;

        if (clientList.size() < 2) {
          /*
          2명까지는 플레이어로 인식
           */
          person = new Player(socket);
        } else {
          /*
          2명 이후부터는 옵저버(관찰자)로 인식
           */
          person = new Observer(socket);
        }

        clientList.add(person); // 일단 모든 접속자들은 `clientList`에 집어 넣음.
        person.start(); // TODO: Fix design.

        OthelloServer.getInstance().printTextToServer("현재 참가자 수 " + clientList.size());

      } catch (Exception e) {
        OthelloServer.getInstance().printTextToServer("accept() error");
        e.printStackTrace();
      }
    }
  }

  /*
   * Broadcast to all the clients. (whether it's player or observer)
   */
  public void broadcast(int code, Optional<String> message) {
    for (Person person : clientList) {
      person.say(new GeneralResponse(code, person, message));
    }
  }

  // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
  public byte[] makePacket(String msg) {
    byte[] packet = new byte[BUF_LEN];

    for (int i = 0; i < BUF_LEN; i++) packet[i] = 0;
    try {
      packet = msg.getBytes("euc-kr");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return packet;
  }

  public void login(Person person) {
    OthelloServer.getInstance().printTextToServer("새로운 참가자 " + person.getUserName() + " 입장.\n");
    String message = "[" + person.getUserName() + "]님이 입장 하였습니다.\n";
    broadcast(PC.getInstance().convert(ProtocolNumber.RESPONSE_101), Optional.of(message));
  }

  public void logout(Person person) {
    clientList.remove(person);
    String message = "[" + person.getUserName() + "]님이\n ";
    broadcast(PC.getInstance().convert(ProtocolNumber.QUIT_CONNECT_203), Optional.of(message));
    OthelloServer.getInstance().printTextToServer("사용자 " + "[" + person.getUserName() + "] 퇴장.\n");
    OthelloServer.getInstance().printTextToServer("현재 참가자 수 " + clientList.size() + "\n");
  }

  public void getInRoom(String roomName, Person person) {
    var list = RoomManager.rooms.get(roomName);
    list.add(person);
    RoomManager.rooms.put(roomName, list);
  }
}
