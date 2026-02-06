package flightbooking.gui.user.theme;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class UserTheme {
    public static final Color PRIMARY = Color.decode("#E51937"); // chủ đạo
    public static final Color ACCENT  = Color.decode("#102469"); // điểm nhấn
    public static final Color BG      = Color.decode("#F7F8FB");
    public static final Color CARD    = Color.WHITE;

    public static void apply() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // font chung
        Font f = new Font("Segoe UI", Font.PLAIN, 13);
        for (Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object val = UIManager.get(key);
            if (val instanceof FontUIResource) UIManager.put(key, new FontUIResource(f));
        }

        UIManager.put("Panel.background", BG);
        UIManager.put("Button.background", PRIMARY);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Table.selectionBackground", PRIMARY);
        UIManager.put("Table.selectionForeground", Color.WHITE);
    }
}
