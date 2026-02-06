package flightbooking.gui.user.pnl;

import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import java.awt.*;

public class PnlThongTinHanhKhach extends JPanel {

    private final AppNavigator nav;

    public static String HO_TEN = "";
    public static String SO_GIAY_TO = "";

    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSoGiayTo = new JTextField();

    public PnlThongTinHanhKhach(AppNavigator nav) {
        this.nav = nav;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        setBackground(UserTheme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JLabel lb = new JLabel("Thông tin hành khách");
        lb.setFont(lb.getFont().deriveFont(Font.BOLD, 22f));
        lb.setForeground(UserTheme.PRIMARY);
        return lb;
    }

    private JComponent buildCard() {
        JPanel card = new JPanel(new GridLayout(2,2,12,12));
        card.setBackground(UserTheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        card.add(label("Họ tên"));
        card.add(txtHoTen);
        card.add(label("Số giấy tờ"));
        card.add(txtSoGiayTo);

        return card;
    }

    private JLabel label(String s) {
        JLabel lb = new JLabel(s);
        lb.setForeground(UserTheme.ACCENT);
        return lb;
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        JButton back = ghostButton("← Quay lại");
        JButton next = primaryButton("Xác nhận →");

        back.addActionListener(e -> nav.show("CHON_GHE"));
        next.addActionListener(e -> {
            HO_TEN = txtHoTen.getText().trim();
            SO_GIAY_TO = txtSoGiayTo.getText().trim();
            if (HO_TEN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên.");
                return;
            }
            nav.show("XAC_NHAN");
        });

        p.add(back);
        p.add(next);
        return p;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(UserTheme.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10,16,10,16));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(UserTheme.CARD);
        b.setForeground(UserTheme.ACCENT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(UserTheme.ACCENT));
        b.setPreferredSize(new Dimension(120, 36));
        return b;
    }
}
