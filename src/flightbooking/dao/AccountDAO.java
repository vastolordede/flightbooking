package flightbooking.dao;

import flightbooking.dto.AccountDTO;
import java.sql.*;

public class AccountDAO {

    // --- BẠN PHẢI THAY 3 DÒNG NÀY BẰNG THÔNG TIN THẬT TRÊN NEON ---
    private final String URL = "jdbc:postgresql://ep-spring-salad-a1ra5tb8-pooler.ap-southeast-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_NO8vqlZ4hoIL&sslmode=require&channelBinding=require";
    private final String USER_DB = "neondb_owner"; 
    private final String PASS_DB = "npg_NO8vqlZ4hoIL";

    public AccountDTO getAccountByUsername(String tenDangNhap) {
        AccountDTO account = null;
        // Sử dụng đúng tên bảng taikhoannhanvien (viết thường) như trong hình Neon
        String sql = "SELECT * FROM taikhoannhanvien WHERE tendangnhap = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER_DB, PASS_DB);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                account = new AccountDTO();
                // Lấy đúng tên cột viết thường trên Neon
                account.setTenDangNhap(rs.getString("tendangnhap"));
                account.setMatKhauMaHoa(rs.getString("matkhau_mahoa"));
                account.setNhomQuyenId(rs.getInt("nhomquyen_id"));
                account.setDangHoatDong(rs.getBoolean("danghoatdong"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối Neon: " + e.getMessage());
            e.printStackTrace();
        }
        return account;
    }
    
    public boolean insertAccount(AccountDTO account) {
        // Đổi tên bảng thành taikhoannhanvien cho đồng bộ
        String sql = "INSERT INTO taikhoannhanvien (nhanvien_id, tendangnhap, matkhau_mahoa, nhomquyen_id, danghoatdong) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL, USER_DB, PASS_DB);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Lưu ý: nhanvien_id bạn phải lấy từ một nhân viên có thật
            ps.setInt(1, 1); 
            ps.setString(2, account.getTenDangNhap());
            ps.setString(3, account.getMatKhauMaHoa());
            ps.setInt(4, account.getNhomQuyenId());
            ps.setBoolean(5, true);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}