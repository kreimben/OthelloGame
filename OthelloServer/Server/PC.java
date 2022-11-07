package Server;

/** abbreviation of `Protocol Converter`. */
public class PC {
  private static PC instance;

  private PC() {}

  public static PC getInstance() {
    if (instance == null) instance = new PC();
    return instance;
  }

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
      default:
        return 0;
    }
  }
}
