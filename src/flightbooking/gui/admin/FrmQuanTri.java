package flightbooking.gui.admin;

import flightbooking.gui.admin.common.AppNavigator;
import flightbooking.gui.admin.pnl.PnlDatVeAdmin;
import flightbooking.gui.admin.pnl.PnlNhomQuyen;
import flightbooking.gui.admin.pnl.PnlQuanLyChuyenBay;
import flightbooking.gui.admin.pnl.PnlQuanLyHHK;
import flightbooking.gui.admin.pnl.PnlQuanLyMayBay;
import flightbooking.gui.admin.pnl.PnlQuanLyNhanVien;
import flightbooking.gui.admin.pnl.PnlQuanLySanBay;
import flightbooking.gui.admin.pnl.PnlQuanLyTuyenBay;
import flightbooking.util.AuthUtil;
import flightbooking.util.Permission;
import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;

public class FrmQuanTri extends JFrame {

    private final AppNavigator nav = new AppNavigator();

    public FrmQuanTri() {
        setTitle("Quản trị - FlightBooking");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        int nhomId = SessionContext.getAdminNhomQuyenId();
        boolean isAdmin = (nhomId == 1);

        // Không phải admin và không có quyền nào thì chặn
        if (!isAdmin && (nhomId == 0 || !SessionContext.hasAnyPermission())) {
            showEmptyScreen();
            return;
        }

        // Register panel
        nav.register("MAY_BAY", new PnlQuanLyMayBay());
        nav.register("SAN_BAY", new PnlQuanLySanBay());
        nav.register("TUYEN_BAY", new PnlQuanLyTuyenBay());
        nav.register("CHUYEN_BAY", new PnlQuanLyChuyenBay());
        nav.register("HANG_HANG_KHONG", new PnlQuanLyHHK());
        nav.register("DAT_VE_ADMIN", new PnlDatVeAdmin());
        nav.register("NHAN_VIEN", new PnlQuanLyNhanVien());
        nav.register("NHOM_QUYEN", new PnlNhomQuyen());

        // Màn hình mặc định
        if (isAdmin) {
            nav.show("NHAN_VIEN");
        } else if (AuthUtil.hasPermission(Permission.SAN_BAY)) {
            nav.show("SAN_BAY");
        } else if (AuthUtil.hasPermission(Permission.TUYEN_BAY)) {
            nav.show("TUYEN_BAY");
        } else if (AuthUtil.hasPermission(Permission.CHUYEN_BAY)) {
            nav.show("CHUYEN_BAY");
        } else if (AuthUtil.hasPermission(Permission.DAT_VE_ADMIN)) {
            nav.show("DAT_VE_ADMIN");
        } else if (AuthUtil.hasPermission(Permission.HANG_HANG_KHONG)) {
            nav.show("HANG_HANG_KHONG");
        } else if (AuthUtil.hasPermission(Permission.MAY_BAY)) {
            nav.show("MAY_BAY");
        } else if (AuthUtil.hasPermission(Permission.NHAN_VIEN)) {
            nav.show("NHAN_VIEN");
        } else if (AuthUtil.hasPermission(Permission.NHOM_QUYEN)) {
            nav.show("NHOM_QUYEN");
        } else {
            showEmptyScreen();
            return;
        }

        JPanel sidebar = buildSidebar();
        JPanel content = nav.getRoot();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
        split.setDividerLocation(240);
        split.setOneTouchExpandable(true);

        setContentPane(split);
    }

    private JPanel buildSidebar() {
        int nhomId = SessionContext.getAdminNhomQuyenId();
        boolean isAdmin = (nhomId == 1);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("ADMIN MENU");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnMayBay = new JButton("Quản lý Máy bay");
        JButton btnSanBay = new JButton("Quản lý Sân bay");
        JButton btnTuyenBay = new JButton("Quản lý Tuyến bay");
        JButton btnChuyenBay = new JButton("Quản lý Chuyến bay");
        JButton btnHHK = new JButton("Quản lý Hãng hàng không");
        JButton btnDatVe = new JButton("Đặt vé (quầy)");
        JButton btnNhanVien = new JButton("Quản lý nhân viên");
        JButton btnNhomQuyen = new JButton("Quản lý nhóm quyền");

        JButton[] buttons = {
                btnSanBay, btnTuyenBay, btnChuyenBay,
                btnDatVe, btnHHK, btnMayBay, btnNhanVien, btnNhomQuyen
        };

        for (JButton b : buttons) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        p.add(title);
        p.add(Box.createVerticalStrut(12));

        if (isAdmin || AuthUtil.hasPermission(Permission.SAN_BAY)) {
            p.add(btnSanBay);
            p.add(Box.createVerticalStrut(8));
        }

        if (isAdmin || AuthUtil.hasPermission(Permission.TUYEN_BAY)) {
            p.add(btnTuyenBay);
            p.add(Box.createVerticalStrut(8));
        }

        if (isAdmin || AuthUtil.hasPermission(Permission.CHUYEN_BAY)) {
            p.add(btnChuyenBay);
            p.add(Box.createVerticalStrut(8));
        }

        if (isAdmin || AuthUtil.hasPermission(Permission.DAT_VE_ADMIN)) {
            p.add(btnDatVe);
            p.add(Box.createVerticalStrut(8));
        }

        if (isAdmin || AuthUtil.hasPermission(Permission.HANG_HANG_KHONG)) {
            p.add(btnHHK);
            p.add(Box.createVerticalStrut(8));
        }

        if (isAdmin || AuthUtil.hasPermission(Permission.MAY_BAY)) {
            p.add(btnMayBay);
            p.add(Box.createVerticalStrut(8));
        }

        if (isAdmin || AuthUtil.hasPermission(Permission.NHAN_VIEN)) {
            p.add(btnNhanVien);
            p.add(Box.createVerticalStrut(8));
        }

        if (isAdmin || AuthUtil.hasPermission(Permission.NHOM_QUYEN)) {
            p.add(btnNhomQuyen);
            p.add(Box.createVerticalStrut(8));
        }

        btnMayBay.addActionListener(e -> nav.show("MAY_BAY"));
        btnSanBay.addActionListener(e -> nav.show("SAN_BAY"));
        btnTuyenBay.addActionListener(e -> nav.show("TUYEN_BAY"));
        btnChuyenBay.addActionListener(e -> nav.show("CHUYEN_BAY"));
        btnHHK.addActionListener(e -> nav.show("HANG_HANG_KHONG"));
        btnDatVe.addActionListener(e -> nav.show("DAT_VE_ADMIN"));
        btnNhanVien.addActionListener(e -> nav.show("NHAN_VIEN"));
        btnNhomQuyen.addActionListener(e -> nav.show("NHOM_QUYEN"));

        return p;
    }

    private void showEmptyScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lbl = new JLabel("Tài khoản chưa được cấp quyền", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.RED);

        panel.add(lbl, BorderLayout.CENTER);

        setContentPane(panel);
        revalidate();
        repaint();
    }
}