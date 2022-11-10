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
            default:
                return 0;
        }
    }
}
