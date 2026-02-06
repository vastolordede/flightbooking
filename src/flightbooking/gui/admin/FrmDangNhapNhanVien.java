package flightbooking.gui.admin;

import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class FrmDangNhapNhanVien extends JFrame {

    private final JComboBox<UserItem> cbUser = new JComboBox<>();
    private final JPasswordField txtMatKhau = new JPasswordField();

    // ✅ mock accounts
    private final List<UserItem> MOCK = Arrays.asList(
            new UserItem(1, "admin", "123", "Admin quầy"),
            new UserItem(2, "staff01", "123", "Nhân viên 01"),
            new UserItem(3, "staff02", "123", "Nhân viên 02")
    );

    public FrmDangNhapNhanVien() {
        setTitle("Đăng nhập nhân viên (Mock)");
        setSize(460, 230);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel form = new JPanel(new GridLayout(2,2,10,10));
        form.add(new JLabel("Tài khoản"));
        form.add(cbUser);
        form.add(new JLabel("Mật khẩu"));
        form.add(txtMatKhau);

        for (UserItem u : MOCK) cbUser.addItem(u);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.addActionListener(e -> doLogin());

        root.add(form, BorderLayout.CENTER);
        root.add(btnLogin, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void doLogin() {
        UserItem u = (UserItem) cbUser.getSelectedItem();
        String p = new String(txtMatKhau.getPassword());

        if (u == null) return;
        if (!u.pass.equals(p)) {
            JOptionPane.showMessageDialog(this, "Sai mật khẩu. (hint: 123)");
            return;
        }

        SessionContext.loginMock(u.id, u.displayName);
        dispose();
        new FrmQuanTri().setVisible(true);
    }

    private static class UserItem {
        final int id;
        final String username;
        final String pass;
        final String displayName;

        UserItem(int id, String username, String pass, String displayName) {
            this.id = id;
            this.username = username;
            this.pass = pass;
            this.displayName = displayName;
        }

        @Override public String toString() {
            return displayName + " • " + username + " (ID=" + id + ")";
        }
    }
}
