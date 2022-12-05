package Client;

import Server.PC;
import Server.ProtocolNumber;
import Server.Request.EnterRequest;
import Server.Request.GameRequest;
import Server.Request.GeneralRequest;
import Server.Response.EnterResponse;
import Server.Response.GameResponse;
import Server.Response.GeneralResponse;
import Server.Response.HistoryResponse;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

/**
 * 관심사 분리를 위한 인위적인 View 클래스 입니다.
 */
public class OthelloView implements Serializable {
    private final JLabel k = new JLabel("1"); //게임 시작 이후 양쪽 합 총 몇번 돌을 놓았는지 동시에 누구 차례인지 기록하는 변수. 홀수는 흑돌 차례, 짝수는 백돌 차례를 의미 
    private final int[][] mat = new int[8][8]; //오셀로판에 해당위치가 흑인지 백인지 아니면 비어있는지 기록하는데 쓰임. 0은 돌 없음 1은 흑돌 2는 백돌 존재를 의미
    private JButton jShow; //경우의 수를 보여주거나 꺼주는 토글 버튼
    private JButton jPass; //차례넘기기 버튼. 돌을 놓을 수 있는 칸이 존재하면 차례를 넘길 수 없음
    private JLabel jStat; //흑과 백의 현재 돌 갯수 출력
    private JLabel jNote; // 누구의 차례인지와, 잘못된 수를 두었을 때, 아직 남은 경우의 수가 있는데 패스버튼을 눌렀을때 등 경고메세지 출력 용도
    private JButton[][] jb; //오셀로판의 돌을 놓는 칸이자 이벤트 발생을 위한 버튼들
    private int game = 0; //게임이 끝났는지 아닌지 알려주는 변수. 0이면 게임중 1이면 게임끝
    private boolean shown = false; //경우의 수를 보여주는 jpass버튼은 이 변수를 활용해 토글식으로 작동함. true면 켜짐상태. false면 꺼짐상태

    //----------------------------------------------------------------------------------------
    private String username;
    private String room_name;
    private String ip_addr;
    private String port_no;
    private OthelloBoard board = null;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean isConnected = true;
    private int howManyPersonInRoom = 0;
    private String playerType = "";
    private int typeId;
    private boolean isRecored = false;
    private boolean isGameStarted = false;
    private int frameWidth = 660;
    private boolean isReady = false;
    private boolean isPlayer2Ready = false;

    private List<String> usernames = new ArrayList<String>();

    //----------------------------------------------------------------------------------------

    /**
     * 생성자
     */
    public OthelloView(String username, String room_name, String ip_addr, String port_no) {
        JFrame f = this.initFrame(); //프레임 생성 및 초기화

        jb = initButton(); //오셀로 판 버튼 생성 및 초기화
        for (int i = 0; i < 8; i++) { //프레임에 오셀로판 버튼들을 부착
            for (int j = 0; j < 8; j++) {
                f.add(jb[i][j]);
            }
        }

        this.jStat = initStat(); //스탯 라벨 생성 및 초기화
        f.add(this.jStat);

        this.jNote = initNote(); //노트 라벨 생성 및 초기화
        f.add(this.jNote);

        this.jPass = initPassButton(); //패스 버튼 생성 및 초기화
        f.add(jPass);

        this.jShow = initShowButton(); //쇼 버튼 생성 및 초기화
        f.add(this.jShow);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                jb[i][j].addActionListener(event -> {
                    boolean isMyTurnNow = Integer.parseInt(k.getText()) % 2 == typeId; //흑돌은 홀수, 백돌은 짝수
                    boolean amIAPlayer = playerType.equals("Player"); //플레이어 타입이 "Player"인지
                    if (game == 0 && isGameStarted && isMyTurnNow && amIAPlayer) //game이 0일때와 2명의 플레이어가 접속했을때만 이벤트가 발동
                    {
                        for (int i1 = 0; i1 < 8; i1++) {
                            for (int j1 = 0; j1 < 8; j1++) { //for문으로 오셀로 판을 한바퀴 돌면서 이벤트가 발생한 버튼을 찾는다.
                                if (event.getSource() == jb[i1][j1]) {
                                    if (shown) hide(); //경우의 수가 보여지고 있다면 꺼준다. 유효한 수든 아니든 버튼이 눌리면 무조건 꺼줌
                                    click(i1, j1, Integer.parseInt(k.getText()), true); //실직적으로 이벤트를 처리하는 함수. 인자 : x, y, z(현재 누구 차례인지), 내가 둔 수인지 알려주는 bool값
                                    k.setText(String.valueOf(Integer.parseInt(k.getText()) + 1)); //일단 돌을 놓았으므로, +1을 해줘 차례를 넘긴다
                                }
                            }
                        }
                    }
                });
            }
        }

        JLabel endLabel = new JLabel(); //쓰는 곳이 없음
        f.add(endLabel);
        initialise(); //초기 세팅(2x2 돌)

        //----------------------------------------------------------------------------------------
        //변수 맵핑
        this.username = username;
        this.room_name = room_name;
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        //Board 생성
        int boardPosX = f.getLocation().x + frameWidth; //프레임의 x크기 만큼 더해줌
        int boardPosY = f.getLocation().y;
        board = new OthelloBoard(this, boardPosX, boardPosY);

        //서버 연결
        try {
            //소켓 연결
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            // 유저이름, 방 이름 요청 보냄
            EnterRequest enterRequest = new EnterRequest(null, room_name, username);
            oos.writeObject(enterRequest);
            board.AppendText("서버 연결 성공");
        } catch (Exception e) {
            board.AppendText("서버 연결 실패");
            e.printStackTrace();
        }

        //서버와 통신하는 리슨 클래스 생성 및 쓰레드 동작
        Listen listen = new Listen();
        listen.start();

        //----------------------------------------------------------------------------------------
    }

    public class Listen extends Thread {
        public void run() {
            while (isConnected) {
                try {
                    GeneralResponse response = (GeneralResponse) ois.readObject(); //서버로부터 응답 받음
                    int code = response.code; //응답의 코드

                    switch (code) //코드에 따라서 프로토콜 처리
                    {
                        case 101: //서버로부터 메세지를 받음
                            String message = response.message;
                            board.AppendText(message);
                            OthelloView.playSound("./OthelloClient/assets/bell.wav");
                            break;
                        case 102: //상대방의 돌 정보를 받음
                            int x = ((GameResponse) response).getX();
                            int y = ((GameResponse) response).getY();
                            int z = Integer.parseInt(k.getText());
                            click(x, y, z, false);
                            break;
                        case 204: //누군가 방에 접속함
                            String enteredUsername = ((EnterResponse) response).getUserName(); //접속한 유저의 이름
                            String roomName = ((EnterResponse) response).getRoomName(); //접속한 방의 이름
                            howManyPersonInRoom = Integer.parseInt(response.message); //현재 방에 접속해있는 사람 숫자
                            String[] mylist = ((EnterResponse) response).usernameList.split(",");
                            usernames.clear();
                            for (String name : mylist) usernames.add(name);

                            //board
                            board.setUserlist(((EnterResponse) response).usernameList);

                            //메세지 출력
                            board.AppendText(enteredUsername + "님이 " + roomName + " 에 접속하셨습니다.");
                            board.AppendText("현재 방 인원 수 : " + howManyPersonInRoom);

                            //본인이 처음 방에 들어왔을때 필요한 로직 처리.
                            if (!isRecored) {
                                firstEnterInRoom();
                            }

                            //인원 수 라벨 조정
                            board.setHowManyPersonInRoomText(howManyPersonInRoom);
                            break;
                        case 205: //누군가 접속 종료함
                            String disconnectedUsername = response.message; //접속 종료한 유저의 이름
                            howManyPersonInRoom--; //현재 방의 인원수를 -1 함

                            //board
                            usernames.remove(disconnectedUsername);
                            String nameList = "";
                            for (int i = 0; i < usernames.size(); i++) {
                                if (i == usernames.size() - 1) nameList += usernames.get(i);
                                else nameList += (usernames.get(i) + ",");
                            }
                            board.setUserlist(nameList);

                            //메세지출력
                            board.AppendText(disconnectedUsername + "님이 나가셨습니다."); //메세지 출력
                            board.AppendText("현재 방 인원 수 : " + howManyPersonInRoom); //메세지 출력

                            //인원 수 라벨 조정
                            board.setHowManyPersonInRoomText(howManyPersonInRoom);
                            break;
                        case 302: //서버로부터 히스토리를 받음
                            String history = ((HistoryResponse) response).getHistory();
                            loadHistory(history);
                        case 401: //게임시작
                            isGameStarted = true;
                            board.disableReadyBtn();
                            board.AppendText(response.message);
                            OthelloView.playSound("./OthelloClient/assets/go.wav");
                            break;
                        case 403: //게임 종료
                            isGameStarted = false;
                            board.AppendText(response.message);
                            break;
                        case 502: //Player2가 준비 버튼을 누름
                            if (typeId == 1) {
                                isPlayer2Ready = !isPlayer2Ready;
                                String player2name = response.message;
                                String text = isPlayer2Ready ? "가 준비했습니다." : "가 준비를 해제했습니다.";
                                board.AppendText(player2name + text);
                            }
                            break;
                        case 602:
                            String leftUserType = response.message;
                            if (leftUserType.equals("1")) {
                                board.AppendText("Player2 퇴장. Player1 승리");
                            } else {
                                board.AppendText("Player1 퇴장. Player2 승리");
                            }
                            int endCode = PC.getInstance().convert(ProtocolNumber.GameEnd_402);
                            GeneralRequest gameOverRequest = new GeneralRequest(endCode, null, "플레이어 퇴장. 경기 중단");
                            oos.writeObject(gameOverRequest);
                            break;

                    }
                } catch (Exception e) {
                    //board.AppendText("서버로부터 수신 실패");
                    e.printStackTrace();
                }
            }
        }
    }

    public void readyToggle() {
        try {
            if (typeId == 1) {
                if (isPlayer2Ready) GameStart();
                else board.AppendText("상대방이 준비 상태가 아닙니다.");
            } else {
                isReady = !isReady;
                board.setReadyBtnText(isReady);
                //준비 상태를 모두에게 알림
                int code = PC.getInstance().convert(ProtocolNumber.READY_REQUEST_501);
                GeneralRequest readyRequest = new GeneralRequest(code, null, username);
                oos.writeObject(readyRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GameStart() {
        try {
            int gameStartCode = PC.getInstance().convert(ProtocolNumber.GameStart_400);
            GeneralRequest gameStartRequest = new GeneralRequest(gameStartCode, null, "Game Start");
            oos.writeObject(gameStartRequest);
        } catch (Exception e) {
            board.AppendText("게임 시작 에러");
            e.printStackTrace();
        }
    }

    public void firstEnterInRoom() {
        try {
            //타입
            playerType = howManyPersonInRoom <= 2 ? "Player" : "Observer";
            typeId = howManyPersonInRoom;
            if (howManyPersonInRoom == 2) typeId = 0;
            if (playerType.equals("Observer")) typeId -= 2;
            isRecored = true;

            //준비 버튼 초기화
            board.initReadyBtn(playerType, typeId);

            //메세지 출력
            String myInfo = "";
            if (playerType.equals("Player")) {
                //메세지
                String stone = typeId == 1 ? "흑돌" : "백돌";
                String attack = typeId == 1 ? "선공" : "후공";
                String id = typeId == 1 ? "1" : "2";
                String typeInfo = playerType + id;
                myInfo = String.format("[SYSTEM] : 당신은 %s입니다. %s, %s을 가집니다.", typeInfo, stone, attack);
            } else {
                myInfo = String.format("[SYSTEM] : 당신은 %s입니다. 관전 및 채팅이 가능합니다.", playerType + Integer.toString(typeId));
            }
            board.AppendText(myInfo);

            //상대방을 기다리는중(Player1) || 히스토리 불러오기(Observer)
            if (playerType.equals("Player")) {
                if (typeId == 1) board.AppendText("상대방을 기다리는 중...");
            } else {
                int historyRequestCode = PC.getInstance().convert(ProtocolNumber.HISTORY_REQUEST_301);
                GeneralRequest historyRequest = new GeneralRequest(historyRequestCode, null, username);
                oos.writeObject(historyRequest);
            }
        } catch (Exception e) {
            board.AppendText("방 입장 에러");
            e.printStackTrace();
        }
    }

    public void loadHistory(String history) {
        String[] list = history.split(":");
        for (int i = 0; i < list.length; i++) {
            String[] detail = list[i].split(",");
            int x = Integer.parseInt(detail[0]);
            int y = Integer.parseInt(detail[1]);
            click(x, y, i + 1, false);
        }
    }

    public String getUserName() {
        return username;
    }

    public void sendChatMessage(String message) {
        try {
            int code = PC.getInstance().convert(ProtocolNumber.CHAT_104);
            GeneralRequest chatMessageRequest = new GeneralRequest(code, null, message);
            oos.writeObject(chatMessageRequest);
        } catch (Exception e) {
            board.AppendText("메세지 송신 오류");
            e.printStackTrace();
        }

    }

    public void Discconect() {
        try {
            isConnected = false;
            int code = PC.getInstance().convert(ProtocolNumber.QUIT_CONNECT_203);
            GeneralRequest quitRequest = new GeneralRequest(code, null, username);
            oos.writeObject(quitRequest);

            if (playerType.equals("Player")) {
                System.out.println("Player Quit");
                int giveUpcode = PC.getInstance().convert(ProtocolNumber.PLAYER_GIVEUP_REQ_601);
                GeneralRequest giveUpRequest = new GeneralRequest(giveUpcode, null, Integer.toString(typeId));
                oos.writeObject(giveUpRequest);
            }

            exit(0);
        } catch (Exception e) {
            board.AppendText("접속 종료 요청 에러..강제 종료 하세요");
            e.printStackTrace();
        }
    }

    /**
     * 프레임 객체를 초기화 합니다.
     *
     * @return 프레임 객체
     */
    public JFrame initFrame() {
        JFrame f = new JFrame("Othello PvP");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //우측 상단 x누르면 프로세스 종료 처리
        f.getContentPane().setBackground(new Color(80, 48, 12));
        f.pack();
        f.setBackground(new Color(80, 48, 12));
        f.setSize(frameWidth, 830);
        f.setResizable(false);
        f.setVisible(true);
        return f;
    }

    /**
     * 보드(버튼)들을 초기화 합니다.
     *
     * @return 버튼들
     */
    public JButton[][] initButton() {
        this.jb = new JButton[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                jb[i][j] = new JButton("");
                jb[i][j].setMargin(new Insets(0, 0, 0, 0));
                jb[i][j].setOpaque(true);
                jb[i][j].setBackground(new Color(78, 146, 82));
                jb[i][j].setForeground(Color.white); // FIXME: test
                jb[i][j].setBounds(40 + 70 * j, 40 + 70 * i, 70, 70);
                jb[i][j].setBorder(new LineBorder(new Color(0, 90, 0)));
                jb[i][j].setFont(new Font("Courier", Font.PLAIN, 80));
                jb[i][j].setPreferredSize(new Dimension(70, 70));
            }
        }

        return jb;
    }


    /**
     * JLabel 객체들을 초기화 합니다.
     *
     * @return JLabel 객체들
     */
    public JLabel initStat() {
        this.jStat = new JLabel("");
        jStat.setOpaque(true);
        jStat.setBounds(2, 640, 636, 46);
        jStat.setBackground(new Color(51, 0, 7));
        jStat.setForeground(new Color(240, 230, 181));
        jStat.setText("White - 2    Black - 2");
        jStat.setFont(new Font("Arial", Font.PLAIN, 40));
        jStat.setBorder(BorderFactory.createEmptyBorder());
        jStat.setHorizontalAlignment(SwingConstants.CENTER);

        return jStat;
    }

    /**
     * 노트들을 초기화 합니다.
     *
     * @return 노트들
     */
    public JLabel initNote() {
        this.jNote = new JLabel("");
        jNote.setOpaque(true);
        jNote.setBounds(2, 690, 636, 46);
        jNote.setBackground(new Color(51, 0, 7));
        jNote.setForeground(new Color(240, 230, 181));
        jNote.setText("Black's turn");
        jNote.setFont(new Font("Arial", Font.PLAIN, 24));
        jNote.setBorder(BorderFactory.createEmptyBorder());
        jNote.setHorizontalAlignment(SwingConstants.CENTER);

        return jNote;
    }

    /**
     * 패스 버튼을 초기화 합니다.
     *
     * @return 패스버튼
     */
    public JButton initPassButton() {
        this.jPass = new JButton("");
        jPass.setMargin(new Insets(0, 0, 0, 0));
        jPass.setOpaque(true);
        jPass.setBounds(4, 740, 314, 46);
        jPass.setBackground(new Color(51, 0, 7));
        jPass.setForeground(new Color(240, 230, 181));
        jPass.setText("PASS MOVE");
        jPass.setFont(new Font("Arial", Font.BOLD, 24));
        jPass.setBorder(new LineBorder(new Color(240, 230, 181)));
        jPass.addActionListener(event -> {
            if (playerType.equals("Observer")) return;
            if (isGameStarted == false) return;
            if (Integer.parseInt(k.getText()) % 2 != typeId) return;

            if (shown) hide(); //패스 버튼을 누르면 무조건 경우의 수 꺼줌
            pass();
        });

        return jPass;
    }

    /**
     * 쇼 버튼들 초기화 합니다.
     *
     * @return 쇼버튼
     */
    public JButton initShowButton() {
        this.jShow = new JButton("");
        jShow.setMargin(new Insets(0, 0, 0, 0));
        jShow.setOpaque(true);
        jShow.setBounds(322, 740, 314, 46);
        jShow.setBackground(new Color(51, 0, 7));
        jShow.setForeground(new Color(240, 230, 181));
        jShow.setText("SHOW MOVE");
        jShow.setFont(new Font("Arial", Font.BOLD, 24));
        jShow.setBorder(new LineBorder(new Color(240, 230, 181)));
        jShow.addActionListener(event -> {
            if (playerType.equals("Observer")) return;
            if (isGameStarted == false) return;
            if (Integer.parseInt(k.getText()) % 2 != typeId) return;

            if (shown) hide(); //켜져있으면 꺼줌
            else show(); //꺼져있으면 켜줌
        });

        return jShow;
    }

    /**
     * 패스할때 계산을 합니다.
     * 돌을 둘 수 있는 곳이 없을 때만 차례 넘기기 가능
     */
    private void pass() {
        int z = Integer.parseInt(k.getText()); //현재 누구 차례인지 가져온다
        if ((z % 2 != 0 && noValid(1, 0)) || (z % 2 == 0 && noValid(2, 0)))
            k.setText(String.valueOf(z + 1)); //돌을 둘 수 있는 칸이 없는지 검사 후 맞다면 k를 1 더해 차례를 넘긴다.
        else jNote.setText((jNote.getText()).substring(0, 12) + "  [Valid move possible]"); //돌을 둘 수 있는 칸이 있다면 경고메세지 출력
    }

    /**
     * 앞으로 둘 수가 유효한지 검사를 합니다.
     *
     * @param p
     * @param r
     * @return 유효한지에 대해 boolean 반환
     * 이 함수 안에서 경우의 수 배경색 전환 처리
     */
    private boolean noValid(int p, int r) {
        int i, j, x, y, m, n, c;
        int q = 3 - p;
        boolean ret = true;
        for (x = 0; x < 8; x++) {
            for (y = 0; y < 8; y++) {
                for (i = 0; i < 8; i++) {
                    for (j = 0; j < 8; j++) {
                        if (mat[i][j] == p) {
                            if (j == y && i < x - 1) {
                                c = 0;
                                for (int k = i + 1; k < x; k++)
                                    if (mat[k][y] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (j == y && i > x + 1) {
                                c = 0;
                                for (int k = x + 1; k < i; k++)
                                    if (mat[k][y] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i == x && j < y - 1) {
                                c = 0;
                                for (int k = j + 1; k < y; k++)
                                    if (mat[x][k] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i == x && j > y + 1) {
                                c = 0;
                                for (int k = y + 1; k < j; k++)
                                    if (mat[x][k] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (x - i == y - j && x - i > 1 && y - j > 1) {
                                c = 0;
                                for (m = i + 1, n = j + 1; m < x && n < y; m++, n++)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (x - i == j - y && x - i > 1 && j - y > 1) {
                                c = 0;
                                for (m = i + 1, n = j - 1; m < x && n > y; m++, n--)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i - x == y - j && i - x > 1 && y - j > 1) {
                                c = 0;
                                for (m = i - 1, n = j + 1; m > x && n < y; m--, n++)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i - x == j - y && i - x > 1 && j - y > 1) {
                                c = 0;
                                for (m = i - 1, n = j - 1; m > x && n > y; m--, n--)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 앞으로의 경우의 수를 보여주는 메서드.
     */
    private void show() {
        int z = Integer.parseInt(k.getText()); //현재 누구 차례인지 가져온다
        /*boolean val =*/
        noValid(2 - (z % 2), 1); //오셀로판을 한바퀴 돌며 경우의 수를 검사하고 그에 맞게 배경색을 바꿔준다.
        this.shown = true; //경우의 수가 켜졌으므로 true로 바꿔줌
        jShow.setText("HIDE MOVE"); //show 버튼의 텍스트를 수정
    }

    /**
     * 앞으로 둘 수 있는 경우의 수를 감추는 메서드
     */
    private void hide() {
        for (int i = 0; i < 8; i++) //오셀로판을 한바퀴 돌며 모든 버튼들의 배경색을 경우의 수 꺼짐 상태로 바꿔줌
            for (int j = 0; j < 8; j++)
                jb[i][j].setBackground(new Color(78, 146, 82));
        shown = false; //경우의 수가 꺼졌으므로 false로 바꿔줌
        jShow.setText("SHOW MOVE"); //show 버튼의 텍스트를 수정
    }


    private ImageIcon getBlackStone() throws IOException {
        var image = new ImageIcon("./OthelloClient/assets/black_stone.png");
        return new ImageIcon(image.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    }

    private ImageIcon getWhiteStone() throws IOException {
        var image = new ImageIcon("./OthelloClient/assets/white_stone.png");
        return new ImageIcon(image.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
    }

    /**
     * 한 수를 두었을 때 처리를 담당하는 함수
     *
     * @param x 가로위치
     * @param y 세로위치
     * @param z 해당 위치가 흑인지 백인지
     */
    private void click(int x, int y, int z, boolean isMine) {
        //----- 선 조치
        int f = mat[x][y]; //마우스를 누른 그 칸에 흑돌이 있는지 백돌이 있는지 돌이 없는지를 받아옴
        if (z % 2 != 0) { //현재 흑돌 차례라면
            try {
                jb[x][y].setIcon(this.getBlackStone());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mat[x][y] = 1; //흑돌이 있음을 기록
        } else {
            try {
                jb[x][y].setIcon(this.getWhiteStone());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mat[x][y] = 2; //백돌이 있음을 기록
        }
        // ----- 선 조치

        // ----- 후 수정
        int e = 0; //놓은 수의 유효성을 알려줄 변수. 0이 유지된다면, 유효하지 않은 수를 의미, 0이 아니라면 유효한 수를 의미
        if (f == 0) e = flip(x, y, z); //마우스를 누른 그 칸에 돌이 없었을 때만 발동. 돌을 뒤집는 로직 실행
        if (e == 0) { //돌이 이미 있는 칸에 돌을 두었거나(f가 0이 아니라 위 줄이 실행안됨), 돌을 둘 수 없는 곳을 눌렀을때(f는 0이나 e가 0을 반환받았을 때 -> 오셀로는 돌을 옳은 곳에 두면 무조건 상대 돌을 뒤집을 수 밖에 없음)
            //유효하지 않은 수로, 차례가 넘어가지 않으며 돌을 다시 두어야함
            k.setText(String.valueOf(Integer.parseInt(k.getText()) - 1)); //k를 -1해서 차례가 넘어가지 않게 해줌
            mat[x][y] = f; //유효하지 않은 수이니, 원래 돌이 없던 곳엔 다시 0을, 흑돌은 1을, 백돌은 2를 기록해준다.
            if (f == 0) jb[x][y].setIcon(null);
            jNote.setText((jNote.getText()).substring(0, 12) + "  [Invalid move, try again]"); //유효하지 않은 수라고 경고메세지 출력
        } else {
            //유효한 수 이므로 차례가 넘어감
            if (isMine) {
                try {
                    GameRequest gameRequest = new GameRequest(null, username, x, y);
                    oos.writeObject(gameRequest);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else
                k.setText(String.valueOf(Integer.parseInt(k.getText()) + 1));
            if (z % 2 != 0) jNote.setText("White's turn"); //돌을 둔 사람이 흑이면 백의 차례라고 출력
            else jNote.setText("Black's turn"); //돌을 둔 사람이 백이면 흑의 차례라고 출력
        }
        //----- 후 수정
        count(); //오셀로 판의 놓인 돌의 갯수를 갱신하고 게임 종료 조건을 검사함

        OthelloView.playSound("./OthelloClient/assets/stone_effect.wav");
    }

    private static void playSound(String path) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(path)));
            clip.start();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }


    /**
     * 뒤집는 효과를 내는 함수
     *
     * @param x 가로위치
     * @param y 세로위치
     * @param z 해당 위치가 흑인지 백인지
     * @return 결과값
     */
    private int flip(int x, int y, int z) {
        int i, j, m, n, d = 0;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if (z % 2 != 0 && mat[i][j] == 1) {
                    if (j == y && i < x - 1) {
                        int c = 0;
                        for (int k = i + 1; k < x; k++)
                            if (mat[k][y] != 2) c++;
                        if (c == 0) {
                            for (int k = i + 1; k < x; k++) {
                                mat[k][y] = 1;
//                                jb[k][y].setForeground(Color.black);
                                try {
                                    jb[k][y].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (j == y && i > x + 1) {
                        int c = 0;
                        for (int k = x + 1; k < i; k++)
                            if (mat[k][y] != 2) c++;
                        if (c == 0) {
                            for (int k = x + 1; k < i; k++) {
                                mat[k][y] = 1;
//                                jb[k][y].setForeground(Color.black);
                                try {
                                    jb[k][y].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i == x && j < y - 1) {
                        int c = 0;
                        for (int k = j + 1; k < y; k++)
                            if (mat[x][k] != 2) c++;
                        if (c == 0) {
                            for (int k = j + 1; k < y; k++) {
                                mat[x][k] = 1;
//                                jb[x][k].setForeground(Color.black);
                                try {
                                    jb[x][k].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i == x && j > y + 1) {
                        int c = 0;
                        for (int k = y + 1; k < j; k++)
                            if (mat[x][k] != 2) c++;
                        if (c == 0) {
                            for (int k = y + 1; k < j; k++) {
                                mat[x][k] = 1;
//                                jb[x][k].setForeground(Color.black);
                                try {
                                    jb[x][k].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (x - i == y - j && x - i > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i + 1, n = j + 1; m < x && n < y; m++, n++)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j + 1; m < x && n < y; m++, n++) {
                                mat[m][n] = 1;
//                                jb[m][n].setForeground(Color.black);
                                try {
                                    jb[m][n].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (x - i == j - y && x - i > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i + 1, n = j - 1; m < x && n > y; m++, n--)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j - 1; m < x && n > y; m++, n--) {
                                mat[m][n] = 1;
//                                jb[m][n].setForeground(Color.black);
                                try {
                                    jb[m][n].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i - x == y - j && i - x > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i - 1, n = j + 1; m > x && n < y; m--, n++)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j + 1; m > x && n < y; m--, n++) {
                                mat[m][n] = 1;
//                                jb[m][n].setForeground(Color.black);
                                try {
                                    jb[m][n].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i - x == j - y && i - x > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i - 1, n = j - 1; m > x && n > y; m--, n--)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j - 1; m > x && n > y; m--, n--) {
                                mat[m][n] = 1;
//                                jb[m][n].setForeground(Color.black);
                                try {
                                    jb[m][n].setIcon(this.getBlackStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                }
                if (z % 2 == 0 && mat[i][j] == 2) {
                    if (j == y && i < x - 1) {
                        int c = 0;
                        for (int k = i + 1; k < x; k++)
                            if (mat[k][y] != 1) c++;
                        if (c == 0) {
                            for (int k = i + 1; k < x; k++) {
                                mat[k][y] = 2;
//                                jb[k][y].setForeground(Color.white);
                                try {
                                    jb[k][y].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (j == y && i > x + 1) {
                        int c = 0;
                        for (int k = x + 1; k < i; k++)
                            if (mat[k][y] != 1) c++;
                        if (c == 0) {
                            for (int k = x + 1; k < i; k++) {
                                mat[k][y] = 2;
//                                jb[k][y].setForeground(Color.white);
                                try {
                                    jb[k][y].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i == x && j < y - 1) {
                        int c = 0;
                        for (int k = j + 1; k < y; k++)
                            if (mat[x][k] != 1) c++;
                        if (c == 0) {
                            for (int k = j + 1; k < y; k++) {
                                mat[x][k] = 2;
//                                jb[x][k].setForeground(Color.white);
                                try {
                                    jb[x][k].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i == x && j > y + 1) {
                        int c = 0;
                        for (int k = y + 1; k < j; k++)
                            if (mat[x][k] != 1) c++;
                        if (c == 0) {
                            for (int k = y + 1; k < j; k++) {
                                mat[x][k] = 2;
//                                jb[x][k].setForeground(Color.white);
                                try {
                                    jb[x][k].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (x - i == y - j && x - i > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i + 1, n = j + 1; m < x && n < y; m++, n++)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j + 1; m < x && n < y; m++, n++) {
                                mat[m][n] = 2;
//                                jb[m][n].setForeground(Color.white);
                                try {
                                    jb[m][n].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (x - i == j - y && x - i > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i + 1, n = j - 1; m < x && n > y; m++, n--)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j - 1; m < x && n > y; m++, n--) {
                                mat[m][n] = 2;
//                                jb[m][n].setForeground(Color.white);
                                try {
                                    jb[m][n].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i - x == y - j && i - x > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i - 1, n = j + 1; m > x && n < y; m--, n++)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j + 1; m > x && n < y; m--, n++) {
                                mat[m][n] = 2;
//                                jb[m][n].setForeground(Color.white);
                                try {
                                    jb[m][n].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                    if (i - x == j - y && i - x > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i - 1, n = j - 1; m > x && n > y; m--, n--)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j - 1; m > x && n > y; m--, n--) {
                                mat[m][n] = 2;
//                                jb[m][n].setForeground(Color.white);
                                try {
                                    jb[m][n].setIcon(this.getWhiteStone());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            d++;
                        }
                    }
                }
            }
        }
        return d;
    }

    /**
     * 바둑판을 초기 값으로 세팅하는 메서드
     */
    private void initialise() {
        try {
            System.out.println(this.getWhiteStone().getImage().getSource());
            jb[3][3].setIcon(this.getWhiteStone());
            jb[4][4].setIcon(this.getWhiteStone());
            jb[3][4].setIcon(this.getBlackStone());
            jb[4][3].setIcon(this.getBlackStone());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                mat[i][j] = 0;
        mat[3][3] = 2;
        mat[4][4] = 2;
        mat[3][4] = 1;
        mat[4][3] = 1;
    }

    /**
     * 결과를 세는 함수
     */
    private void count() {
        int b = 0; //흑돌 개수
        int w = 0; //백돌 개수
        for (int i = 0; i < 8; i++) //오셀로 판을 한바퀴 돌며 현재 존재하는 돌의 갯수를 기록
            for (int j = 0; j < 8; j++) {
                if (mat[i][j] == 1) b++; //1이면 흑돌 + 1
                if (mat[i][j] == 2) w++; //2면 백돌 + 1
            }
        jStat.setText("White - " + w + "    Black - " + b); //현재 오셀로 판에 있는 흑돌, 백돌의 개수를 출력
        if (b + w == 64) { //흑돌과 백돌의 합이 64가 되면(모든 칸이 채워짐) 게임 끝
            if (b > w) jNote.setText("Black WINS (All squares filled)"); //흑돌이 백돌보다 많으면 흑돌 승 메세지 출력
            else if (w > b) jNote.setText("White WINS (All squares filled)"); //백돌이 흑돌보다 많으면 백돌 승 메세지 출력
            else jNote.setText("Game TIED (All squares filled)"); //흑돌과 백돌의 수가 같으면 무승부 메세지 출력
            game = 1; //game이 1이 되어 버튼을 클릭해도 이벤트 처리가 되지 않도록 함

            try {
                int endCode = PC.getInstance().convert(ProtocolNumber.GameEnd_402);
                GeneralRequest gameOverRequest = new GeneralRequest(endCode, null, jNote.getText());
                oos.writeObject(gameOverRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (noValid(1, 0) && noValid(2, 0)) { //오셀로 판을 한바퀴 돌며 유효성 검사를 해서 한쪽이라도 더 이상 둘 수 없는 상태가 되면
            if (b > w) jNote.setText("Black WINS (No valid moves left)"); //흑돌이 백돌보다 많으면 흑돌 승 메세지 출력
            else if (w > b) jNote.setText("White WINS (No valid moves left)"); //백돌이 흑돌보다 많으면 백돌 승 메세지 출력
            else jNote.setText("Game TIED (No valid moves left)"); //흑돌과 백돌의 수가 같으면 무승부 메세지 출력
            game = 1; //game이 1이 되어 버튼을 클릭해도 이벤트 처리가 되지 않도록 함

            try {
                int endCode = PC.getInstance().convert(ProtocolNumber.GameEnd_402);
                GeneralRequest gameOverRequest = new GeneralRequest(endCode, null, jNote.getText());
                oos.writeObject(gameOverRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
