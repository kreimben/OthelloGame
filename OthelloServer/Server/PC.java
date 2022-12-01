package Server;

/**
 * abbreviation of `Protocol Converter`.
 */
public class PC {
    private static PC instance;

    private PC() {

    }

    public static PC getInstance() {
        if (instance == null) instance = new PC();
        return instance;
    }

    // 프로토콜을 정의한 enum을 만들었는데 그걸 int로 바꿔줄 함수와 클래스 입니다.
    public int convert(ProtocolNumber p) {
        switch (p) {
            case PLAY_WITH_COORDINATE_100:
                return 100;
            case RESPONSE_101:
                return 101;
            case PLAYED_WITH_COORDINATE_102:
                return 102;
            case CHAT_104:
                return 104;
            case CREATE_ROOM_200:
                return 200;
            case ROOM_CREATED_201:
                return 201;
            case ENTER_ROOM_202:
                return 202;
            case QUIT_CONNECT_203:
                return 203;
            case ENTERED_ROOM_204:
                return 204;
            case QUIT_CONNECT_205:
                return 205;
            case HISTORY_REQUEST_301:
                return 301;
            case HISTORY_RESPONSE_302:
                return 302;
            case GameStart_400:
                return 400;
            case GameStart_401:
                return 401;
            case GameEnd_402:
                return 402;
            case GameEnd_403:
                return 403;
            case READY_REQUEST_501:
                return 501;
            case READY_RESPONSE_502:
                return 502;
            case PLAYER_GIVEUP_REQ_601:
                return 601;
            case PLAYER_GIVEUP_RES_602:
                return 602;
            default:
                return 0;
        }
    }
}
