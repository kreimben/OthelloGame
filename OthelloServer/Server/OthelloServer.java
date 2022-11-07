package Server;

import Server.Exceptions.AlreadyRoomExistsException;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.Arrays;

/**
 * 서버 메인 클래스
 */
public class OthelloServer extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private static OthelloServer instance;
    private final JPanel contentPane;
    private final JTextField portNumberTextField;
    private final JTextArea textArea;

    /**
     * Create the frame.
     */
    private OthelloServer() {
        // 기본 설정
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 500);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // 스크롤
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setMinimumSize(new Dimension(500, 200));
        contentPane.add(scrollPane);

        // 서버가 받는 로그(메세지)를 보여줌.
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setMinimumSize(new Dimension(500, 300));
        scrollPane.setViewportView(textArea);

        // 포트 넘버를 받는 레이블
        JLabel portNumberLabel = new JLabel("Port Number");
        contentPane.add(portNumberLabel);

        // 포트 넘버를 받는 text field
        portNumberTextField = new JTextField();
        portNumberTextField.setHorizontalAlignment(SwingConstants.CENTER);
        portNumberTextField.setText("30000");
        portNumberTextField.setMaximumSize(new Dimension(500, 20));
        contentPane.add(portNumberTextField);

        // Start Server button.
        JButton startServerButton = new JButton("Server listening");
        startServerButton.addActionListener(e -> {
            try {
                // Create socket.
                var port = Integer.parseInt(portNumberTextField.getText());
                var manager = new RoomManager(port);
                manager.start();
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                printTextToServer("Please input valid number.");
            }

            printTextToServer("Othello Server Running...");
            startServerButton.setText("Othello Server Running..");
            startServerButton.setEnabled(false);
            portNumberTextField.setEnabled(false);
        });
        contentPane.add(startServerButton);

        // 방 이름을 받는 레이블
        JLabel roomNameLabel = new JLabel("Room Name");
        contentPane.add(roomNameLabel);

        // 방 이름을 받는 Text Field
        var roomNameTextField = new JTextField();
        roomNameTextField.setHorizontalAlignment(SwingConstants.CENTER);
        roomNameTextField.setText("room");
        roomNameTextField.setMaximumSize(new Dimension(500, 20));
        contentPane.add(roomNameTextField);

        // Create Room button.
        JButton createRoomButton = new JButton("Create Room");
        createRoomButton.addActionListener(e -> {
            var roomName = roomNameTextField.getText();
            printTextToServer("\n새로운 방을 만들고 있습니다...");
            printTextToServer("방 이름: " + roomName);
            try {
                RoomManager.makeNewRoom(roomName);
            } catch (AlreadyRoomExistsException ex) {
                ex.printStackTrace();
                printTextToServer("같은 방의 이름이 이미 존재합니다.");
            }
            printTextToServer("방 정보: " + Arrays.toString(RoomManager.rooms.entrySet().toArray()));
        });
        contentPane.add(createRoomButton);
    }

    public static OthelloServer getInstance() {
        if (instance == null) instance = new OthelloServer();
        return instance;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                OthelloServer frame = new OthelloServer();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void printTextToServer(String str) {
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }
}
