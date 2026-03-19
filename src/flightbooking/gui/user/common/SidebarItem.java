package flightbooking.gui.user.common;

import javax.swing.*;
import java.awt.*;

public class SidebarItem extends JButton {

    public SidebarItem(String text, Runnable action) {
        setText(text);
        setFocusPainted(false);
        setHorizontalAlignment(SwingConstants.LEFT);

        setBackground(new Color(40, 40, 40));
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addActionListener(e -> action.run());

        // hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(60, 60, 60));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(40, 40, 40));
            }
        });
    }
}