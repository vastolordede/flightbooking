    package flightbooking.gui.admin;

    import flightbooking.gui.admin.common.AppNavigator;
    import flightbooking.gui.admin.pnl.*;

    import javax.swing.*;
    import java.awt.*;

    public class FrmQuanTri extends JFrame {

        private final AppNavigator nav = new AppNavigator();

        public FrmQuanTri() {
            setTitle("Quản trị - FlightBooking");
            setSize(1100, 720);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JPanel sidebar = buildSidebar();
            JPanel content = nav.getRoot();

            nav.register("MAY_BAY", new PnlQuanLyMayBay()); 
            nav.register("SAN_BAY", new PnlQuanLySanBay());
            nav.register("TUYEN_BAY", new PnlQuanLyTuyenBay());
            nav.register("CHUYEN_BAY", new PnlQuanLyChuyenBay());
            nav.register("HANG_HANG_KHONG", new PnlQuanLyHHK());
            nav.register("DAT_VE_ADMIN", new PnlDatVeAdmin());

            nav.show("SAN_BAY");

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
            split.setDividerLocation(240);
            split.setOneTouchExpandable(true);

            setContentPane(split);
        }

        private JPanel buildSidebar() {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            JLabel title = new JLabel("ADMIN MENU");
            title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton btnMayBay = new JButton("Quản lý Máy bay");
            JButton btnSanBay = new JButton("Quản lý Sân bay");
            JButton btnTuyenBay = new JButton("Quản lý Tuyến bay");
            JButton btnChuyenBay = new JButton("Quản lý Chuyến bay");
            JButton btnHHK = new JButton("Quản lý Hãng hàng không");
            JButton btnDatVe = new JButton("Đặt vé (quầy)");

            for (JButton b : new JButton[]{btnSanBay, btnTuyenBay, btnChuyenBay,btnHHK, btnDatVe}) {
                b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                b.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            btnMayBay.addActionListener(e -> nav.show("MAY_BAY"));
            btnSanBay.addActionListener(e -> nav.show("SAN_BAY"));
            btnTuyenBay.addActionListener(e -> nav.show("TUYEN_BAY"));
            btnChuyenBay.addActionListener(e -> nav.show("CHUYEN_BAY"));
            btnHHK.addActionListener(e -> nav.show("HANG_HANG_KHONG"));
            btnDatVe.addActionListener(e -> nav.show("DAT_VE_ADMIN"));

            p.add(title);
            p.add(Box.createVerticalStrut(12));
            p.add(btnSanBay);
            p.add(Box.createVerticalStrut(8));
            p.add(btnTuyenBay);
            p.add(Box.createVerticalStrut(8));
            p.add(btnChuyenBay);
            p.add(Box.createVerticalStrut(8));
            p.add(btnDatVe);
            p.add(Box.createVerticalStrut(8));
            p.add(btnHHK);
            p.add(Box.createVerticalStrut(8));
            p.add(btnMayBay);

            return p;
        }
    }
