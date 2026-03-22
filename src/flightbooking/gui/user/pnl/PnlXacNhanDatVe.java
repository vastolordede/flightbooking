package flightbooking.gui.user.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.bus.ThongTinVeBUS;
import flightbooking.dto.ThongTinVeDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.common.TempVeStore;
import flightbooking.gui.user.theme.UserTheme;
import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PnlXacNhanDatVe extends JPanel {

    private final AppNavigator nav;
    private final DatVeBUS datVeBUS = new DatVeBUS();

    private final JTextArea summary = new JTextArea();
    private final JComboBox<String> cbHanhKhach = new JComboBox<>();

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
        summary.setText("Đang tải thông tin vé...");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UserTheme.BG);

        wrapper.add(cbHanhKhach, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(summary), BorderLayout.CENTER);

        return wrapper;
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        JButton back = ghostButton("← Quay lại");
        JButton addMore = ghostButton("+ Thêm vé");
        JButton done = primaryButton("Hoàn tất");

        back.addActionListener(e -> nav.show("HANH_KHACH"));

        // 🔥 ADD MORE VÉ
        addMore.addActionListener(e -> {
            nav.show("CHON_GHE");
        });

        done.addActionListener(e -> datVe());

        p.add(back);
        p.add(addMore);
        p.add(done);
        return p;
    }

    private void datVe() {
        try {
            int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;

            List<DatVeBUS.ThongTinHanhKhachVaGhe> items = TempVeStore.getAll();

            if (items.isEmpty()) {
                throw new RuntimeException("Bạn chưa thêm vé nào.");
            }

            Integer khachHangId = SessionContext.getCurrentUserId();

            if (khachHangId == null) {
                throw new RuntimeException("Bạn chưa đăng nhập.");
            }

            datVeBUS.datVe(
                    khachHangId,
                    null,
                    chuyenBayId,
                    items,
                    "online"
            );

            JOptionPane.showMessageDialog(this, "Đặt vé thành công!");

            // 🔥 RESET TOÀN BỘ FLOW
            TempVeStore.clear();
            PnlChonGhe.GHE_ID_DANG_CHON = null;
            PnlThongTinHanhKhach.HO_TEN = "";
            PnlThongTinHanhKhach.SO_GIAY_TO = "";

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

    // =============================
    // 🔥 LOAD DATA
    // =============================
    public void reload() {

        int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;
        List<DatVeBUS.ThongTinHanhKhachVaGhe> ds = TempVeStore.getAll();

        if (chuyenBayId <= 0 || ds.isEmpty()) {
            summary.setText("❌ Thiếu dữ liệu.");
            return;
        }

        cbHanhKhach.removeAllItems();

        int index = 1;
        for (DatVeBUS.ThongTinHanhKhachVaGhe it : ds) {
            cbHanhKhach.addItem("Hành khách " + index++);
        }

        // 🔥 khi chọn dropdown → load vé tương ứng
        cbHanhKhach.addActionListener(e -> {
            int selected = cbHanhKhach.getSelectedIndex();
            if (selected >= 0 && selected < ds.size()) {
                showSingle(ds.get(selected));
            }
        });

        // load mặc định vé đầu
        showSingle(ds.get(0));
    }

    // =============================
    // 🔥 SHOW 1 VÉ
    // =============================
    private void showSingle(DatVeBUS.ThongTinHanhKhachVaGhe it) {

        int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;

        ThongTinVeBUS bus = new ThongTinVeBUS();

        ThongTinVeDTO t = bus.getFullInfo(
                chuyenBayId,
                it.getGheId(),
                it.getHanhKhach().getHoTen(),
                it.getHanhKhach().getSoGiayTo()
        );

        if (t == null) {
            summary.setText("Không có dữ liệu");
            return;
        }

        summary.setText(
                "===== VÉ =====\n\n" +
                "Hành khách: " + t.getHoTen() + "\n" +
                "Giấy tờ: " + t.getSoGiayTo() + "\n\n" +

                "Tuyến: " + t.getSanBayDi() + " → " + t.getSanBayDen() + "\n" +
                "Khởi hành: " + t.getGioKhoiHanh() + "\n\n" +

                "Máy bay: " + t.getTenMayBay() + " - " + t.getKieuMayBay() + "\n\n" +

                "Ghế: " + t.getTenGhe() + "\n" +
                "Hạng: " + t.getHangGhe() + "\n"
        );
    }
}