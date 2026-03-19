package flightbooking.gui.user.common;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AppNavigator {

    private final JPanel root = new JPanel(new BorderLayout()); // 🔥 đổi ở đây
    private final JPanel content = new JPanel(new CardLayout()); // chứa màn hình

    private final Map<String, JComponent> screens = new HashMap<>();

    public AppNavigator() {
        // gắn sidebar 1 lần duy nhất
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
    }

    public JPanel getRoot() {
        return root;
    }

    public void register(String name, JComponent comp) {
        screens.put(name, comp);
        content.add(comp, name);
    }

    public void show(String name) {
        ((CardLayout) content.getLayout()).show(content, name);
    }

    // 🔥 SIDEBAR DUY NHẤT
    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(10, 1, 5, 5));
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setPreferredSize(new Dimension(180, 0));

        sidebar.add(new SidebarItem("🔍 Tìm chuyến", () -> show("TIM_CHUYEN")));
        sidebar.add(new SidebarItem("✈️ Kết quả", () -> show("KQ_CHUYEN")));
        sidebar.add(new SidebarItem("💺 Chọn ghế", () -> show("CHON_GHE")));
        sidebar.add(new SidebarItem("👤 Hành khách", () -> show("HANH_KHACH")));
        sidebar.add(new SidebarItem("✅ Xác nhận", () -> show("XAC_NHAN")));

        return sidebar;
    }
}