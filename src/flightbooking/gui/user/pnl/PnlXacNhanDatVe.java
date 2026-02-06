package flightbooking.gui.user.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.dto.HanhKhachDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PnlXacNhanDatVe extends JPanel {

    private final AppNavigator nav;
    private final DatVeBUS datVeBUS = new DatVeBUS();

    private final JTextArea summary = new JTextArea();

    public PnlXacNhanDatVe(AppNavigator nav) {
        this.nav = nav;

        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        setBackground(UserTheme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSummary(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JLabel lb = new JLabel("Xác nhận đặt vé");
        lb.setFont(lb.getFont().deriveFont(Font.BOLD, 22f));
        lb.setForeground(UserTheme.PRIMARY);
        return lb;
    }

    private JComponent buildSummary() {
        summary.setEditable(false);
        summary.setLineWrap(true);
        summary.setWrapStyleWord(true);
        summary.setBackground(UserTheme.CARD);
        summary.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        refreshSummary();
        return new JScrollPane(summary);
    }

    private void refreshSummary() {
        String gheText = (PnlChonGhe.GHE_TEXT_DA_CHON != null && !PnlChonGhe.GHE_TEXT_DA_CHON.isBlank())
                ? PnlChonGhe.GHE_TEXT_DA_CHON
                : (PnlChonGhe.GHE_ID_DA_CHON != null ? ("GheID=" + PnlChonGhe.GHE_ID_DA_CHON) : "(chưa chọn)");

        summary.setText(
                "Tóm tắt đặt vé\n\n" +
                        "Chuyến bay ID: " + PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON + "\n" +
                        "Ghế: " + gheText + "\n" +
                        "Họ tên: " + PnlThongTinHanhKhach.HO_TEN + "\n" +
                        "Số giấy tờ: " + PnlThongTinHanhKhach.SO_GIAY_TO + "\n\n" +
                        "Bấm 'Hoàn tất' để tạo vé."
        );
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        JButton back = ghostButton("← Quay lại");
        JButton done = primaryButton("Hoàn tất");

        back.addActionListener(e -> nav.show("HANH_KHACH"));
        done.addActionListener(e -> datVe());

        p.add(back);
        p.add(done);
        return p;
    }

    private void datVe() {
        try {
            int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;

            Integer gheIdObj = PnlChonGhe.GHE_ID_DA_CHON;
            if (gheIdObj == null || gheIdObj <= 0) {
                throw new RuntimeException("Bạn chưa chọn ghế hợp lệ.");
            }
            int gheId = gheIdObj;

            DatVeBUS.ThongTinHanhKhachVaGhe it = new DatVeBUS.ThongTinHanhKhachVaGhe();

            HanhKhachDTO hk = new HanhKhachDTO();
            hk.setHoTen(PnlThongTinHanhKhach.HO_TEN);
            hk.setSoGiayTo(PnlThongTinHanhKhach.SO_GIAY_TO);

            it.setHanhKhach(hk);
            it.setGheId(gheId);

            List<DatVeBUS.ThongTinHanhKhachVaGhe> items = new ArrayList<>();
            items.add(it);

            datVeBUS.datVe(
                    null,          // taiKhoanKhachHangId (nếu có login thì truyền)
                    null,          // taiKhoanNhanVienId (online thường null)
                    chuyenBayId,
                    items,
                    "online"
            );

            JOptionPane.showMessageDialog(this, "Đặt vé thành công!");
            // reset chọn ghế để lần sau không dính
            PnlChonGhe.GHE_ID_DA_CHON = null;
            PnlChonGhe.GHE_TEXT_DA_CHON = "";

            nav.show("TIM_CHUYEN");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đặt vé thất bại: " + ex.getMessage());
        }
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
