package flightbooking.gui.user.pnl;

import flightbooking.dto.KhachHangDTO;
import flightbooking.gui.user.FrmMyTickets;

import javax.swing.*;
import java.awt.*;

public class PnlUserInfo extends JPanel {

    public PnlUserInfo(KhachHangDTO user) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        setBackground(new Color(245, 245, 245));

        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Arial", Font.PLAIN, 40));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel("Tên: " + safe(user.getHoTen()));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEmail = new JLabel("Email: " + safe(user.getTenDangNhap()));
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 🔥 FIX: tạm thời lấy điểm từ DAO nếu DTO chưa có
        JLabel lblDiem = new JLabel("Điểm: " + user.getDiemTichLuy());
lblDiem.setAlignmentX(Component.CENTER_ALIGNMENT);
JButton btnMyTickets = new JButton("🎫 Vé của tôi");
btnMyTickets.setAlignmentX(Component.CENTER_ALIGNMENT);



        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogout.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new flightbooking.gui.user.FrmDangNhapUser().setVisible(true);
        });
        btnMyTickets.addActionListener(e -> {
    new FrmMyTickets(user.getTaiKhoanKhachHangId()).setVisible(true);
});

        add(lblAvatar);
        add(Box.createVerticalStrut(15));
        add(lblName);
        add(Box.createVerticalStrut(10));
        add(lblEmail);
        add(Box.createVerticalStrut(10));
        add(lblDiem);
        add(Box.createVerticalStrut(20));
        add(btnMyTickets);
add(Box.createVerticalStrut(20));
        add(btnLogout);
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}