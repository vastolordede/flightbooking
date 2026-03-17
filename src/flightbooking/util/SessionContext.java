package flightbooking.util;
import flightbooking.dto.KhachHangDTO;

public class SessionContext {
    private static String userDemo; // Cho admin
    private static KhachHangDTO currentUser; // Cho khách hàng

    // ===== Admin =====
    public static void setUserDemo(String user) {
        userDemo = user;
    }

    public static String getUserDemo() {
        return userDemo;
    }

    public static void clearAdminSession() {
        userDemo = null;
    }

    // ===== User/Customer =====
    public static void setCurrentUser(KhachHangDTO user) {
        currentUser = user;
    }

    public static KhachHangDTO getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getHanhkhachId() : -1;
    }

    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getTenDangNhap() : null;
    }

    public static void clearUserSession() {
        currentUser = null;
    }

    // ===== General =====
    public static void clearAll() {
        userDemo = null;
        currentUser = null;
    }
}
