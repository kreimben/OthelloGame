package Server;

// 프로토콜을 정의한 enum입니다. 통신에 직접적으로 쓰이지 않습니다.
public enum ProtocolNumber {
    /**
     * 100 좌표를 이용해 플레이 함 request. c -> s
     * 101 서버로부터 메세지를 받음 response. s -> c
     * 102 좌표를 이용해 플레이 함 response s -> c
     * 104 채팅 request. c -> s
     * 200 방을 만듬 request. c -> s
     * 201 방이 만들어짐 response. s -> c
     * 202 방에 입장 request. c -> s
     * 203 접속 종료 request. c -> s
     * 204 방에 입장 response. s -> c
     * 205 접속 종료 response. s -> c
     * 301 히스토리 요청 request. c -> s
     * 302 히스토리 응답 response. s -> c
     * 400 게임 시작 요청 request. c->s
     * 401 게임 시작 응답 response. s->c
     * 402 게임 끝 요청 request. c->s
     * 403 게임 끝 응답 response. s->c
     */
    PLAY_WITH_COORDINATE_100,
    RESPONSE_101,
    PLAYED_WITH_COORDINATE_102,
    CHAT_104,
    CREATE_ROOM_200,
    ROOM_CREATED_201,
    ENTER_ROOM_202,
    QUIT_CONNECT_203,
    ENTERED_ROOM_204,
    QUIT_CONNECT_205,
    HISTORY_REQUEST_301,
    HISTORY_RESPONSE_302,
    GameStart_400,
    GameStart_401,
    GameEnd_402,
    GameEnd_403,
}
