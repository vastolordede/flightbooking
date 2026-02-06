package flightbooking.gui.user.common;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AppNavigator {
    private final JPanel root = new JPanel(new CardLayout());
    private final Map<String, JComponent> screens = new HashMap<>();

    public JPanel getRoot() { return root; }

    public void register(String name, JComponent comp) {
        screens.put(name, comp);
        root.add(comp, name);
    }

    public void show(String name) {
        ((CardLayout) root.getLayout()).show(root, name);
    }
}
