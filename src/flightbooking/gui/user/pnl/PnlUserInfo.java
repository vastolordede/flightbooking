package flightbooking.gui.user.pnl;

import flightbooking.dto.KhachHangDTO;

import javax.swing.*;
import java.awt.*;

public class PnlUserInfo extends JPanel {

    public PnlUserInfo(KhachHangDTO user) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        setBackground(new Color(245, 245, 245));

        // ===== AVATAR =====
        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(100, 100));
        lblAvatar.setMaximumSize(new Dimension(100, 100));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 👉 nếu chưa có ảnh thì dùng text
        lblAvatar.setText("👤");
        lblAvatar.setFont(new Font("Arial", Font.PLAIN, 40));

        // ===== NAME =====
        JLabel lblName = new JLabel("Tên: " + safe(user.getHoTen()));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== EMAIL =====
        JLabel lblEmail = new JLabel("Email: " + safe(user.getTenDangNhap()));
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ===== LOGOUT =====
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogout.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new flightbooking.gui.user.FrmDangNhapUser().setVisible(true);
        });

        // ===== ADD =====
        add(lblAvatar);
        add(Box.createVerticalStrut(15));
        add(lblName);
        add(Box.createVerticalStrut(10));
        add(lblEmail);
        add(Box.createVerticalStrut(20));
        add(btnLogout);
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}