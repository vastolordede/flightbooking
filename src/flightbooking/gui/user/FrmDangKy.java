package flightbooking.gui.user;

import flightbooking.bus.KhachHangBUS; // Dùng KhachHangBUS thay vì AccountBUS
import flightbooking.gui.user.theme.UserTheme;
import flightbooking.util.UiUtil; // Dùng Utility bạn vừa ghép
import javax.swing.*;
import java.awt.*;

public class FrmDangKy extends JFrame {
    // Thêm các ô nhập liệu mới để khớp với Database
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSDT = new JTextField();
    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();
    
    private final KhachHangBUS khachHangBus = new KhachHangBUS();

    public FrmDangKy() {
        setTitle("Đăng ký tài khoản mới");
        setSize(400, 450); // Tăng chiều cao để chứa thêm ô nhập liệu
        setLocationRelativeTo(null);
        
        // Sửa GridLayout thành 6 hàng để đủ chỗ
        JPanel root = new JPanel(new GridLayout(6, 2, 10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        root.setBackground(UserTheme.BG);

        // Thêm các trường dữ liệu bắt buộc của khách hàng
        root.add(new JLabel("Họ và tên:")); root.add(txtHoTen);
        root.add(new JLabel("Số điện thoại:")); root.add(txtSDT);
        root.add(new JLabel("Tên đăng nhập:")); root.add(txtUser);
        root.add(new JLabel("Mật khẩu:")); root.add(txtPass);
        root.add(new JLabel("Nhập lại mật khẩu:")); root.add(txtConfirm);

        JButton btnReg = new JButton("Tạo tài khoản");
        btnReg.addActionListener(e -> handleRegister());
        root.add(new JLabel("")); root.add(btnReg);

        setContentPane(root);
    }

  private void handleRegister() {
    String hoTen = txtHoTen.getText().trim();
    String sdt = txtSDT.getText().trim();
    String u = txtUser.getText().trim();
    String p = new String(txtPass.getPassword());
    String cp = new String(txtConfirm.getPassword());

    if (khachHangBus.dangKy(this, hoTen, sdt, u, p, cp)) {
        this.dispose();
        // ✅ Mở form đăng nhập lại
        new FrmDangNhapUser().setVisible(true);
    }
}
}