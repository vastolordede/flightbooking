package flightbooking.gui.user;

import flightbooking.bus.KhachHangBUS; // THAY ĐỔI: Dùng KhachHangBUS
import flightbooking.util.SessionContext;
import flightbooking.util.UiUtil; // THAY ĐỔI: Dùng UiUtil cho thông báo
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import flightbooking.dto.KhachHangDTO;

public class FrmDangNhapUser extends JFrame {

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    // THAY ĐỔI: Trỏ về BUS của Khách hàng
    private final KhachHangBUS khachHangBus = new KhachHangBUS(); 

    public FrmDangNhapUser() {
        setTitle("FlightBooking - Login");
        setSize(1000, 750); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // --- GIỮ NGUYÊN PHẦN VẼ BACKGROUND BIỂN CỦA BẠN ---
        JPanel pnlBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon img = new ImageIcon(getClass().getResource("/resources/images/bien.jpg"));
                    g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
                } catch (Exception e) {
                    g.setColor(new Color(135, 206, 235)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        pnlBackground.setLayout(new GridBagLayout()); 

        // --- GIỮ NGUYÊN PHẦN WHITE BOX CỦA BẠN ---
        JPanel whiteBox = new JPanel();
        whiteBox.setPreferredSize(new Dimension(420, 550));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setLayout(new BoxLayout(whiteBox, BoxLayout.Y_AXIS));
        whiteBox.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));

        JLabel lblTitle = new JLabel("[ FlightBooking ]");
        lblTitle.setFont(new Font("Serif", Font.PLAIN, 28));
        lblTitle.setForeground(new Color(25, 25, 112));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblWelcome = new JLabel("<html><center>Chào Mừng Đến Với<br>Hệ Thống Đặt Vé Máy Bay Trực Tuyến<br><br>Vui lòng đăng nhập để tiếp tục</center></html>");
        lblWelcome.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblWelcome.setForeground(new Color(25, 25, 112));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 0, 25, 0));

        Dimension inputSize = new Dimension(Integer.MAX_VALUE, 45);
        txtUsername.setMaximumSize(inputSize);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        txtPassword.setMaximumSize(inputSize);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 30, 60), 1), BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        Dimension btnSize = new Dimension(280, 45);

        JButton btnSummit = new JButton("Đăng Nhập"); 
        btnSummit.setBackground(new Color(230, 30, 60)); 
        btnSummit.setForeground(Color.WHITE); // Đổi thành màu trắng cho dễ đọc trên nền đỏ
        btnSummit.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSummit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSummit.setMaximumSize(btnSize);
        btnSummit.setFocusPainted(false);
        btnSummit.addActionListener(e -> handleLogin()); // Gọi hàm login mới

        JButton btnGoToReg = new JButton("Đăng ký tài khoản mới");
        btnGoToReg.setBackground(Color.WHITE);
        btnGoToReg.setForeground(new Color(230, 30, 60));
        btnGoToReg.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnGoToReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGoToReg.setMaximumSize(btnSize);
        btnGoToReg.setFocusPainted(false);
        btnGoToReg.setBorder(BorderFactory.createLineBorder(new Color(230, 30, 60), 1));
        // Mở form đăng ký chúng ta vừa sửa
        btnGoToReg.addActionListener(e -> {
            new FrmDangKy().setVisible(true);
            this.dispose(); 
        });

        whiteBox.add(lblTitle);
        whiteBox.add(lblWelcome);
        whiteBox.add(txtUsername);
        whiteBox.add(Box.createVerticalStrut(20)); 
        whiteBox.add(txtPassword);
        whiteBox.add(Box.createVerticalStrut(40)); 
        whiteBox.add(btnSummit);
        whiteBox.add(Box.createVerticalStrut(12)); 
        whiteBox.add(btnGoToReg);

        pnlBackground.add(whiteBox);
        setContentPane(pnlBackground);
    }
    
private void handleLogin() {
    String u = txtUsername.getText().trim();
    String p = new String(txtPassword.getPassword());
    
    // Gọi hàm đăng nhập từ KhachHangBUS
    if (khachHangBus.dangNhap(this, u, p)) {
        // ✅ LẬP TỨC LƯU SESSION
        KhachHangDTO user = khachHangBus.getByUsername(u);
        if (user != null) {
            SessionContext.setCurrentUser(user);
            System.out.println("✓ Session saved for user: " + user.getTenDangNhap());
        }
        
        this.dispose();
        new FrmNguoiDung().setVisible(true); 
    } else {
        // UiUtil trong KhachHangBUS sẽ tự động hiện thông báo lỗi rồi
    }
}
}