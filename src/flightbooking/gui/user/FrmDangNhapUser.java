package flightbooking.gui.user;

import flightbooking.gui.user.theme.UserTheme;
import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FrmDangNhapUser extends JFrame {

    private final JTextField txtTenDangNhap = new JTextField();
    private final JPasswordField txtMatKhau = new JPasswordField();

    // MOCK USERS (chỉ login mock theo yêu cầu)
    private static final Map<String, String> MOCK = new HashMap<>();
    static {
        MOCK.put("user1", "123");
        MOCK.put("user2", "123");
        MOCK.put("demo",  "demo");
    }

    public FrmDangNhapUser() {
        setTitle("Đăng nhập người dùng");
        setSize(420, 230);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        root.setBackground(UserTheme.BG);

        JPanel form = new JPanel(new GridLayout(2,2,10,10));
        form.setOpaque(false);
        form.add(new JLabel("Tên đăng nhập"));
        form.add(txtTenDangNhap);
        form.add(new JLabel("Mật khẩu"));
        form.add(txtMatKhau);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.addActionListener(e -> doLogin());

        JLabel hint = new JLabel("Demo: user1/123 hoặc demo/demo");
        hint.setForeground(UserTheme.ACCENT);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(hint, BorderLayout.NORTH);
        bottom.add(btnLogin, BorderLayout.SOUTH);

        root.add(form, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void doLogin() {
        String u = txtTenDangNhap.getText().trim();
        String p = new String(txtMatKhau.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đủ thông tin.");
            return;
        }

        String pw = MOCK.get(u);
        if (pw == null || !pw.equals(p)) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu (mock).");
            return;
        }

        // set session (mock)
        SessionContext.setUserDemo(u);

        dispose();
        new FrmNguoiDung().setVisible(true);
    }
}
