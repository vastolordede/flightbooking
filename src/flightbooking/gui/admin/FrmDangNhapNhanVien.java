package flightbooking.gui.admin;

import flightbooking.bus.AccountBUS;
import flightbooking.dao.NhomQuyenDAO;
import flightbooking.dto.AccountDTO;
import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FrmDangNhapNhanVien extends JFrame {

    private final JTextField txtTenDangNhap = new JTextField();
    private final JPasswordField txtMatKhau = new JPasswordField();

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
        
        JPanel pnlBottom = new JPanel(new GridLayout(1,2,10,10));
pnlBottom.add(btnLogin);


root.add(pnlBottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void doLogin() {

        String u = txtTenDangNhap.getText().trim();
        String p = new String(txtMatKhau.getPassword());

        String validateMsg = accountBus.validateLogin(u, p);
        if (validateMsg != null) {
            JOptionPane.showMessageDialog(this, validateMsg);
            return;
        }

        try {

    AccountDTO account = accountBus.checkLogin(u, p);

    if (account != null) {

        int nhomId = account.getNhomQuyenId();

        SessionContext.setAdminSession(
                account.getTenDangNhap(),
                nhomId,
                account.getTaiKhoanId()
        );

        Set<Integer> perms = new HashSet<>();

if (nhomId != 0) {
    List<Integer> list = new NhomQuyenDAO().getPermissionsByNhom(nhomId);
    perms.addAll(list);
}

SessionContext.setPermissions(perms);
        SessionContext.setPermissions(perms);

        JOptionPane.showMessageDialog(this, "✓ Đăng nhập thành công!");
        dispose();
        new FrmQuanTri().setVisible(true);

    } else {
        JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
    }

} catch (RuntimeException ex) {
    JOptionPane.showMessageDialog(this, ex.getMessage());
}
    }
}