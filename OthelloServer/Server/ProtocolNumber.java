package Server;

// 프로토콜을 정의한 enum입니다. 통신에 직접적으로 쓰이지 않습니다.
public enum ProtocolNumber {
    /**
     * 100 좌포를 이용해 플레이 함 request. c -> s
     * 101 서버로부터 메세지를 받음 response. s -> c
     * 200 방을 만듬 request. c -> s
     * 201 방이 만들어짐 response. s -> c
     * 202 방에 입장 request. c -> s
     * 203 접속 종료 request. c -> s
     */
    PLAY_WITH_COORDINATE_100,
    RESPONSE_101,
    CREATE_ROOM_200,
    ROOM_CREATED_201,
    ENTER_ROOM_202,
    QUIT_CONNECT_203,
}
