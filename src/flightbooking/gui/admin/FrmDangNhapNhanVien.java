package flightbooking.gui.admin;

import flightbooking.bus.AccountBUS;
import flightbooking.dto.AccountDTO;
import flightbooking.util.SessionContext;
import javax.swing.*;
import java.awt.*;

public class FrmDangNhapNhanVien extends JFrame {

    // Thay JComboBox bằng JTextField để nhân viên tự nhập
    private final JTextField txtTenDangNhap = new JTextField();
    private final JPasswordField txtMatKhau = new JPasswordField();
    
    // Gọi lớp BUS bạn vừa hoàn thiện
    private final AccountBUS accountBus = new AccountBUS();

    public FrmDangNhapNhanVien() {
        setTitle("Đăng nhập hệ thống quản trị");
        setSize(400, 230);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Tên đăng nhập:"));
        form.add(txtTenDangNhap);
        form.add(new JLabel("Mật khẩu:"));
        form.add(txtMatKhau);

        JButton btnLogin = new JButton("Đăng nhập hệ thống");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.addActionListener(e -> doLogin());

        root.add(new JLabel("HỆ THỐNG QUẢN TRỊ FLIGHTBOOKING", JLabel.CENTER), BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(btnLogin, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void doLogin() {
        String u = txtTenDangNhap.getText().trim();
        String p = new String(txtMatKhau.getPassword());

        // 1. Kiểm tra rỗng qua BUS
        String validateMsg = accountBus.validateLogin(u, p);
        if (validateMsg != null) {
            JOptionPane.showMessageDialog(this, validateMsg);
            return;
        }

        // 2. Kiểm tra tài khoản thật từ Database
        AccountDTO account = accountBus.checkLogin(u, p);

        if (account != null) {
            // Lưu Session: Bạn có thể lưu tên đăng nhập hoặc ID nhân viên
            SessionContext.setUserDemo(account.getTenDangNhap()); 
            
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Chào mừng Quản trị viên.");
            dispose();
            
            // Mở màn hình quản trị chính
            new FrmQuanTri().setVisible(true); 
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản không tồn tại hoặc sai mật khẩu!");
        }
    }
}