package Client;

// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.LineBorder;

public class OthelloBoard extends JFrame {
    private JPanel contentPane;
    private JLabel lblUserName;
    private JTextPane textArea;
    private JTextField txtInput;
    private JButton btnSend;
    private OthelloView view;
    private String username;
    private JLabel howManyPersonInRoom;
    private JButton readyBtn;
    private JLabel userlist;

    public OthelloBoard(OthelloView view, int posX, int posY) {
        this.view = view;
        username = view.getUserName();

        //Frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(posX, posY, 394, 730);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //ScrollPane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 352, 471);
        contentPane.add(scrollPane);

        //TextArea
        textArea = new JTextPane();
        textArea.setEditable(true);
        textArea.setEnabled(false);
        textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
        textArea.setDisabledTextColor(Color.BLACK);
        scrollPane.setViewportView(textArea);

        //유저 이름
        lblUserName = new JLabel(username);
        lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblUserName.setBackground(Color.WHITE);
        lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserName.setBounds(12, 490, 62, 40);
        contentPane.add(lblUserName);

        //채팅 텍스트필드
        txtInput = new JTextField();
        txtInput.setBounds(84, 490, 209, 40);
        contentPane.add(txtInput);
        txtInput.setColumns(10);

        //전송 버튼
        btnSend = new JButton("전송");
        btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
        btnSend.setBounds(300, 490, 69, 40);
        contentPane.add(btnSend);

        //준비 버튼
        readyBtn = new JButton("준비");
        readyBtn.setFont(new Font("굴림", Font.PLAIN, 14));
        readyBtn.setBounds(12, 540, 80, 40);
        contentPane.add(readyBtn);

        //현재 방의 인원수 라벨
        howManyPersonInRoom = new JLabel("현재 인원수 : 1");
        howManyPersonInRoom.setBorder(new LineBorder(new Color(0, 0, 0)));
        howManyPersonInRoom.setBackground(Color.WHITE);
        howManyPersonInRoom.setFont(new Font("굴림", Font.BOLD, 14));
        howManyPersonInRoom.setHorizontalAlignment(SwingConstants.CENTER);
        howManyPersonInRoom.setBounds(102, 540, 150, 40);
        contentPane.add(howManyPersonInRoom);

        //접속 종료 버튼
        JButton btnDisConnect = new JButton("접속 종료");
        btnDisConnect.setBounds(262, 540, 100, 40);
        contentPane.add(btnDisConnect);

        //방에 입장한 유저 이름들 출력
        userlist = new JLabel("유저 리스트 : ");
        userlist.setBorder(new LineBorder(new Color(0, 0, 0)));
        userlist.setBackground(Color.WHITE);
        userlist.setFont(new Font("굴림", Font.BOLD, 11));
        userlist.setHorizontalAlignment(SwingConstants.LEFT);
        userlist.setBounds(12, 590, 360, 40);
        contentPane.add(userlist);

        //버튼 이벤트 등록
        SendChatMessageAction sendChatMessage = new SendChatMessageAction();
        ReadyAction readyAction = new ReadyAction();
        btnSend.addActionListener(sendChatMessage);
        txtInput.addActionListener(sendChatMessage);
        readyBtn.addActionListener(readyAction);

        DisconnectAction disconnectAction = new DisconnectAction();
        btnDisConnect.addActionListener(disconnectAction);

        setVisible(true);
    }
    class SendChatMessageAction implements ActionListener // 접속 종료 이벤트 발생
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == btnSend || e.getSource() == txtInput)
            {
                String input = txtInput.getText();
                String message = String.format("[%s] : %s", username, input);
                txtInput.setText("");
                txtInput.requestFocus();
                view.sendChatMessage(message);
            }

        }
    }

    class DisconnectAction implements ActionListener // 접속 종료 이벤트 발생
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.Discconect();
        }
    }

    class ReadyAction implements ActionListener // 접속 종료 이벤트 발생
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.readyToggle();
        }
    }

    // 화면에 출력
    public void AppendText(String msg) {
        // textArea.append(msg + "\n");
        msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
        int len = textArea.getDocument().getLength();

        // 끝으로 이동
        textArea.setCaretPosition(len);
        textArea.replaceSelection(msg + "\n");
    }

    public void setHowManyPersonInRoomText(int howMany)
    {
        String text = String.format("현재 인원 수 : %d", howMany);
        howManyPersonInRoom.setText(text);
    }

    public void initReadyBtn(String playerType, int typeId)
    {
        if(playerType.equals("Player"))
        {
            String text = typeId == 1 ? "시작" : "준비";
            readyBtn.setText(text);
        }
        else
        {
            readyBtn.setEnabled(false);
        }
    }

    public void setReadyBtnText(boolean isReady)
    {
        String text = isReady ? "준비 해제" : "준비";
        readyBtn.setText(text);
    }

    public void disableReadyBtn()
    {
        readyBtn.setEnabled(false);
    }

    public void setUserlist(String names)
    {
        String text = String.format("방에 입장한 유저들 : %s", names);
        userlist.setText(text);
    }
}
