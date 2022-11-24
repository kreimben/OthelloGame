package Server.Manager;

import Server.Exceptions.AlreadyRoomExistsException;
import Server.OthelloServer;
import Server.PC;
import Server.Person.Observer;
import Server.Person.Person;
import Server.Person.Player;
import Server.ProtocolNumber;
import Server.Request.EnterRequest;
import Server.Request.GameRequest;
import Server.Response.EnterResponse;
import Server.Response.GeneralResponse;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 방을 관리하는 객체로써 서버당 하나의 인스턴스만 존재해야합니다.
public class RoomManager extends Thread implements Serializable {

    private static final int BUF_LEN = 128; // 예제코드에서 가져온거라 없어질 수도 있습니다.
    private static Map<String, ArrayList<Person>> rooms; // 방 이름과 해당 방에 접속한 클라이언트를 기록합니다.

    public static Map<String, ArrayList<Person>> getRooms() {
        return RoomManager.rooms;
    }

    public static Map<String, ArrayList<GameRequest>> history; // 방 이름과 해당 방의 대국을 기록합니다.
    public static ServerSocket serverSocket; // port number로 만든 서버 소켓 객체입니다.
    private final ArrayList<Person> clientList = new ArrayList(); // 현재 접속한 모든 클라이언트를 기록합니다.

    public RoomManager(int port) {
        try {
            RoomManager.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RoomManager.rooms = new HashMap<>();
        RoomManager.history = new HashMap<>();
    }

    // static 함수로써 새로운 방을 만들기 위해 쓰입니다.
    public static void makeNewRoom(String roomName) throws AlreadyRoomExistsException {
        if (RoomManager.rooms.get(roomName) != null) {
            throw new AlreadyRoomExistsException("Already this room exists.");
        } else {
            RoomManager.rooms.put(roomName, new ArrayList<>());
        }
        RoomManager.rooms.forEach((key, value) -> System.out.println("key: " + key + " / " + "value" + value));
    }


    @Override
    public void run() {
        /*
         * 그냥 계속해서 접속자를 받음.
         */
        while (true) {
            try {
                //클라이언트 연결 받음
                OthelloServer.getInstance().printTextToServer("새로운 참가자를 기다리는 중...");
                // client에서는 서버에 접속하자마자 어떤 방 이름에 들어갈 것인지 `roomName`에 넣어줘야 합니다.
                // user name은 `userName`에 넣어주면 됩니다.
                var socket = serverSocket.accept();
                var ois = new ObjectInputStream(socket.getInputStream());
                var oos = new ObjectOutputStream(socket.getOutputStream());

                //클라이언트로부터 입장 요청을 받음
                EnterRequest req = (EnterRequest) ois.readObject();
                String roomName = req.getRoomName();
                String userName = req.getUserName();
                OthelloServer.getInstance().printTextToServer("새로운 참가자 [" + userName + "] from " + socket);
                OthelloServer.getInstance().printTextToServer(roomName + "방에 들어갑니다.");

                //클라이언트가 플레이어인지 관전자인지 정할 변수
                Person person;

                if (rooms.get(roomName).size() < 2) {
                    /*
                    2명까지는 플레이어로 인식
                     */
                    person = new Player(socket, this);
                } else {
                    /*
                    2명 이후부터는 옵저버(관찰자)로 인식
                     */
                    person = new Observer(socket, this);
                }

                //오브젝트 스트림 맵핑
                person.setOos(oos);
                person.setOis(ois);

                person.setRoomName(roomName); // 모든 `person`객체에도 `roomName`을 입력해줌.
                person.setUserName(userName); //모든 person 객체에도 username을 입력해줌
                clientList.add(person); // 일단 모든 접속자들은 `clientList`에 집어 넣음.
                this.getInRoom(roomName, person); // `roomName`에 따른 방에 `person`을 집어 넣음.
                this.makeHistory(roomName); // `history`에 방이름으로 대국 정보를 만듬.
                person.start();

                OthelloServer.getInstance().printTextToServer("현재 참가자 수 " + clientList.size());

            } catch (Exception e) {
                OthelloServer.getInstance().printTextToServer("accept() error");
                e.printStackTrace();
            }
        }
    }

    /*
     * player이건 observer이건 모두에게 메세지를 보냅니다.
     */
    public void broadcast(Object obj) {
        for (Person person : clientList) {
            person.say(obj);
        }
    }


    // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
    // 예제코드 레거시라서 언제 없어질지 모릅니다.
    public byte[] makePacket(String msg) {
        byte[] packet = new byte[BUF_LEN];

        for (int i = 0; i < BUF_LEN; i++)
            packet[i] = 0;
        try {
            packet = msg.getBytes("euc-kr");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return packet;
    }

    // 예제코드 레거시. 언제 수정되거나 없어질지 모릅니다.
    public void login(Person person) {
        OthelloServer.getInstance().printTextToServer("새로운 참가자 " + person.getUserName() + " 입장.\n");
        String message = "[" + person.getUserName() + "]님이 입장 하였습니다.\n";
        broadcast(
                new GeneralResponse(
                        PC.getInstance().convert(ProtocolNumber.RESPONSE_101),
                        person,
                        message
                ));
    }

    // 예제코드 레거시. 언제 수정되거나 없어질지 모릅니다.
    public void logout(Person person) {
        clientList.remove(person);
        String message = "[" + person.getUserName() + "]님이\n ";
        broadcast(
                new GeneralResponse(
                        PC.getInstance().convert(ProtocolNumber.QUIT_CONNECT_203),
                        person,
                        message));
        OthelloServer.getInstance().printTextToServer("사용자 " + "[" + person.getUserName() + "] 퇴장.\n");
        OthelloServer.getInstance().printTextToServer("현재 참가자 수 " + clientList.size() + "\n");
    }

    // 방에 입장하는 함수입니다. 언제 수정되거나 없어질지 모릅니다.
    public void getInRoom(String roomName, Person person) {
        //리스트 갱신
        var list = RoomManager.rooms.get(roomName);
        list.add(person);
        RoomManager.rooms.put(roomName, list);

        String personCountInRoom = Integer.toString(list.size()); //현재 방에 있는 사람 수
        EnterResponse someoneEnteredResponse = new EnterResponse(person.getUserName(), person.getRoomName(), personCountInRoom); //현재 방에 접속한 사람과, 사람 수의 정보를 제공
        broadcast(someoneEnteredResponse); //모든 클라이언트에게 전송
        /*
        if (list.size() == 2) {
            this.broadcast(
                    new GeneralResponse(
                            PC.getInstance().convert(ProtocolNumber.RESPONSE_101),
                            null,
                            "GameStart"
                    )
            );
        }
         */
    }

    public void makeHistory(String roomName) {
        var list = RoomManager.history.get(roomName);
        if (list == null || list.isEmpty()) {
            RoomManager.history.put(roomName, new ArrayList<GameRequest>());
        }
    }

    // `roomName`을 가진 방을 폭파합니다. 모든 클라이언트와의 접속을 끊습니다.
    public void disconnectAllClient(String roomName) {
        var list = RoomManager.rooms.get(roomName);
        for (Person person : list) {
            try {
                person.getSocket().close();
            } catch (IOException e) {
                OthelloServer.getInstance().printTextToServer("IOException: " + e.getMessage());
            }
        }

        RoomManager.rooms.get(roomName).clear();
        RoomManager.rooms.remove(roomName);

        RoomManager.history.get(roomName).clear();
        RoomManager.history.remove(roomName);
    }
}