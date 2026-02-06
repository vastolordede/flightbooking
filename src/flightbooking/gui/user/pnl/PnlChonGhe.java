package flightbooking.gui.user.pnl;

import flightbooking.bus.DatVeBUS;
import flightbooking.dto.GheDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PnlChonGhe extends JPanel {

    private final AppNavigator nav;
    private final DatVeBUS datVeBUS = new DatVeBUS();

    public static Integer GHE_ID_DA_CHON = null;
    public static String GHE_TEXT_DA_CHON = "";

    private final JLabel lblInfo = new JLabel("Chọn ghế");

    public PnlChonGhe(AppNavigator nav) {
        this.nav = nav;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(UserTheme.BG);

        lblInfo.setForeground(UserTheme.ACCENT);
        add(lblInfo, BorderLayout.NORTH);

        add(buildSeatTabs(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JComponent buildSeatTabs() {
        int chuyenBayId = PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON;

        List<GheDTO> all = datVeBUS.dsGheCuaChuyen(chuyenBayId);

        if (all.isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setBackground(UserTheme.CARD);
            empty.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
            JLabel t = new JLabel("Chuyến này chưa có ghế (DB rỗng) hoặc thiếu maybay_id.");
            t.setForeground(UserTheme.ACCENT);
            empty.add(t, BorderLayout.CENTER);
            return empty;
        }

        // group theo tầng
        Map<Integer, List<GheDTO>> byTang = all.stream()
                .collect(Collectors.groupingBy(g -> g.getTang() == null ? 1 : g.getTang()));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UserTheme.BG);

        // sort tầng tăng dần
        byTang.keySet().stream().sorted().forEach(tang -> {
            List<GheDTO> dsTang = byTang.get(tang);
            tabs.addTab("Tầng " + tang, buildGrid(dsTang));
        });

        return tabs;
    }

    private JComponent buildGrid(List<GheDTO> ds) {
        // Bạn có thể thay layout theo máy bay/hạng ghế sau.
        // Hiện tại: auto grid dựa trên số lượng ghế.
        int n = ds.size();
        int cols = 6; // tạm giống demo A-F
        int rows = (int) Math.ceil(n / (double) cols);

        JPanel grid = new JPanel(new GridLayout(rows, cols, 8, 8));
        grid.setBackground(UserTheme.CARD);
        grid.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        for (GheDTO g : ds.stream()
                .sorted((a,b) -> String.valueOf(a.getTenGhe()).compareToIgnoreCase(String.valueOf(b.getTenGhe())))
                .collect(Collectors.toList())) {

            String seatText = g.getTenGhe(); // ví dụ: E.A1

            JButton b = new JButton(seatText);
            b.setFocusPainted(false);

            if (g.isDaDat() || (g.getTrangThai() != null && g.getTrangThai() == 0)) {
                b.setEnabled(false);
                b.setBackground(new Color(70, 70, 70));
                b.setForeground(Color.LIGHT_GRAY);
            } else {
                b.setBackground(Color.WHITE);
                b.setForeground(UserTheme.ACCENT);
                b.addActionListener(e -> {
                    GHE_ID_DA_CHON = g.getGheId();
                    GHE_TEXT_DA_CHON = "Tầng " + (g.getTang() == null ? 1 : g.getTang()) + " - " + seatText;
                    lblInfo.setText("Đã chọn: " + GHE_TEXT_DA_CHON + " | Chuyến: " + PnlKetQuaChuyenBay.CHUYEN_BAY_ID_CHON);
                });
            }

            grid.add(b);
        }

        return new JScrollPane(grid);
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);

        JButton back = ghostButton("← Quay lại");
        JButton next = primaryButton("Tiếp tục →");

        back.addActionListener(e -> nav.show("KQ_CHUYEN"));
        next.addActionListener(e -> {
            if (GHE_ID_DA_CHON == null) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn ghế.");
                return;
            }
            nav.show("HANH_KHACH");
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
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
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
