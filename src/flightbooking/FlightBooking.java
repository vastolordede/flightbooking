package flightbooking;

import javax.swing.SwingUtilities;

import flightbooking.gui.LoginView;

public class FlightBooking {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
