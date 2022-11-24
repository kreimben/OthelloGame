package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Connect extends JFrame{
  private JFrame frame; //프레임
  private JPanel contentPane; //텍스트 필드들이 붙을 패널
  private JTextField txtUserName; //유저 이름
  private JTextField txtRoomName; //방 이름
  private JTextField txtIpAddress; //ip주소
  private JTextField txtPortNumber; //포트 번호

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Connect frame = new Connect();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Connect()
  {
    //프레임
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 254, 400);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    //유저 이름
    JLabel lblNewLabel = new JLabel("User Name");
    lblNewLabel.setBounds(12, 39, 82, 33);
    contentPane.add(lblNewLabel);

    txtUserName = new JTextField();
    txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
    txtUserName.setBounds(101, 39, 116, 33);
    contentPane.add(txtUserName);
    txtUserName.setColumns(10);

    //방 이름
    JLabel lblRoomName = new JLabel("Room Name");
    lblRoomName.setBounds(12, 100, 82, 33);
    contentPane.add(lblRoomName);

    txtRoomName = new JTextField();
    txtRoomName.setHorizontalAlignment(SwingConstants.CENTER);
    txtRoomName.setColumns(10);
    txtRoomName.setBounds(101, 100, 116, 33);
    contentPane.add(txtRoomName);

    //ip주소
    JLabel lblIpAddress = new JLabel("IP Address");
    lblIpAddress.setBounds(12, 161, 82, 33);
    contentPane.add(lblIpAddress);

    txtIpAddress = new JTextField();
    txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
    txtIpAddress.setText("127.0.0.1");
    txtIpAddress.setColumns(10);
    txtIpAddress.setBounds(101, 161, 116, 33);
    contentPane.add(txtIpAddress);

    //포트 번호
    JLabel lblPortNumber = new JLabel("Port Number");
    lblPortNumber.setBounds(12, 223, 82, 33);
    contentPane.add(lblPortNumber);

    txtPortNumber = new JTextField();
    txtPortNumber.setText("30000");
    txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
    txtPortNumber.setColumns(10);
    txtPortNumber.setBounds(101, 223, 116, 33);
    contentPane.add(txtPortNumber);

    //서버 연결 버튼
    JButton btnConnect = new JButton("Connect");
    btnConnect.setBounds(12, 286, 205, 38);
    contentPane.add(btnConnect);
    
    //이벤트 등록
    Myaction action = new Myaction();
    btnConnect.addActionListener(action);
    txtUserName.addActionListener(action);
    txtRoomName.addActionListener(action);
    txtIpAddress.addActionListener(action);
    txtPortNumber.addActionListener(action);
  }

  //버튼 액션 클래스
  class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
  {
    @Override
    public void actionPerformed(ActionEvent e) {
      String username = txtUserName.getText().trim();
      String room_name = txtRoomName.getText().trim();
      String ip_addr = txtIpAddress.getText().trim();
      String port_no = txtPortNumber.getText().trim();
      OthelloView view = new OthelloView(username, room_name, ip_addr, port_no);
      setVisible(false);
    }
  }
}
