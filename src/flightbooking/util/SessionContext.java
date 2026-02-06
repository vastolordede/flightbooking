package flightbooking.util;

public class SessionContext {

    // ====== NHÂN VIÊN (MOCK) ======
    private static Integer nhanVienId = null;
    private static String nhanVienTen = null;

    // Giữ nguyên để không hỏng code cũ
    public static void loginMock(int id, String ten) {
        nhanVienId = id;
        nhanVienTen = ten;
    }

    public static Integer getNhanVienId() { return nhanVienId; }
    public static String getNhanVienTen() { return nhanVienTen; }

    // ====== USER (MOCK) ======
    private static Integer userId = null;
    private static String userTenDangNhap = null;

    // ✅ Hàm bạn đang gọi trong FrmDangNhapUser
    public static void setUserDemo(String tenDangNhap) {
        userId = 1; // mock id
        userTenDangNhap = tenDangNhap;
    }

    public static Integer getUserId() { return userId; }
    public static String getUserTenDangNhap() { return userTenDangNhap; }

    // ====== LOGOUT ======
    // Giữ logout cũ nhưng mở rộng: xoá cả user
    public static void logout() {
        nhanVienId = null;
        nhanVienTen = null;
        userId = null;
        userTenDangNhap = null;
    }

    // (Tuỳ chọn) logout riêng
    public static void logoutUser() {
        userId = null;
        userTenDangNhap = null;
    }

    public static void logoutNhanVien() {
        nhanVienId = null;
        nhanVienTen = null;
    }
}
