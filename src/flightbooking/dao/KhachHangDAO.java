package flightbooking.dao;

import flightbooking.dto.KhachHangDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO extends BaseDAO {

    // ================= MAP RESULTSET =================
    private KhachHangDTO map(ResultSet rs) throws SQLException {
        KhachHangDTO kh = new KhachHangDTO();
        kh.setHanhkhachId(rs.getInt("hanhkhach_id"));
        kh.setHoTen(rs.getString("hoten"));
        kh.setDienThoai(rs.getString("dienthoai"));
        kh.setTenDangNhap(rs.getString("tendangnhap"));
        kh.setMatKhauMaHoa(rs.getString("matkhau_mahoa"));
        kh.setDangHoatDong(rs.getBoolean("danghoatdong"));
        return kh;
    }

    // ================= KIỂM TRA TÀI KHOẢN ĐÃ TỒN TẠI =================
    public boolean existsByUsername(String tenDangNhap) {
        String sql = "SELECT 1 FROM taikhoankhachhang WHERE LOWER(tendangnhap) = LOWER(?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("khachhang existsByUsername failed", e);
        }
    }

    // ================= ĐĂNG KÝ KHÁCH HÀNG =================
    public boolean register(String hoTen, String sdt, String user, String pass) {
        // Kiểm tra tài khoản đã tồn tại
        if (existsByUsername(user)) {
            return false;
        }

        String sqlInsertKH = "INSERT INTO hanhkhach(hoten, dienthoai) VALUES(?, ?) RETURNING hanhkhach_id";
        String sqlInsertTK = "INSERT INTO taikhoankhachhang(hanhkhach_id, tendangnhap, matkhau_mahoa, danghoatdong) VALUES(?, ?, ?, ?)";

        try (Connection c = getConnection()) {
            c.setAutoCommit(false); // Bắt đầu transaction

            int hanhkhachId = -1;

            // 1. Insert vào bảng hanhkhach
            try (PreparedStatement ps = c.prepareStatement(sqlInsertKH)) {
                ps.setString(1, hoTen.trim());
                ps.setString(2, sdt.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hanhkhachId = rs.getInt(1);
                    }
                }
            }

            if (hanhkhachId == -1) {
                c.rollback();
                return false;
            }

            // 2. Insert vào bảng taikhoankhachhang
            try (PreparedStatement ps = c.prepareStatement(sqlInsertTK)) {
                ps.setInt(1, hanhkhachId);
                ps.setString(2, user.trim());
                ps.setString(3, pass); // Trong thực tế nên mã hóa password
                ps.setBoolean(4, true); // dangHoatDong = true
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    c.commit();
                    return true;
                } else {
                    c.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("khachhang register failed", e);
        }
    }

    // ================= KIỂM TRA ĐĂNG NHẬP =================
    public boolean checkLogin(String user, String pass) {
        String sql = "SELECT 1 FROM taikhoankhachhang " +
                     "WHERE LOWER(tendangnhap) = LOWER(?) " +
                     "AND matkhau_mahoa = ? " +
                     "AND danghoatdong = true";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.trim());
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("khachhang checkLogin failed", e);
        }
    }

    // ================= LẤY THÔNG TIN KHÁCH HÀNG THEO USERNAME =================
    public KhachHangDTO findByUsername(String tenDangNhap) {
        String sql = "SELECT kh.hanhkhach_id, kh.hoten, kh.dienthoai, " +
                     "tk.tendangnhap, tk.matkhau_mahoa, tk.danghoatdong " +
                     "FROM hanhkhach kh " +
                     "INNER JOIN taikhoankhachhang tk ON tk.hanhkhach_id = kh.hanhkhach_id " +
                     "WHERE LOWER(tk.tendangnhap) = LOWER(?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("khachhang findByUsername failed", e);
        }
        return null;
    }

    // ================= LẤY THÔNG TIN KHÁCH HÀNG THEO ID =================
    public KhachHangDTO findById(int hanhkhachId) {
        String sql = "SELECT kh.hanhkhach_id, kh.hoten, kh.dienthoai, " +
                     "tk.tendangnhap, tk.matkhau_mahoa, tk.danghoatdong " +
                     "FROM hanhkhach kh " +
                     "INNER JOIN taikhoankhachhang tk ON tk.hanhkhach_id = kh.hanhkhach_id " +
                     "WHERE kh.hanhkhach_id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, hanhkhachId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("khachhang findById failed", e);
        }
        return null;
    }
    public List<KhachHangDTO> findAll() {
    String sql = "SELECT kh.hanhkhach_id, kh.hoten, kh.dienthoai, " +
                 "tk.tendangnhap, tk.matkhau_mahoa, tk.danghoatdong " +
                 "FROM hanhkhach kh " +
                 "INNER JOIN taikhoankhachhang tk ON tk.hanhkhach_id = kh.hanhkhach_id " +
                 "WHERE tk.danghoatdong = true " +
                 "ORDER BY kh.hanhkhach_id DESC";
    
    List<KhachHangDTO> list = new ArrayList<>();
    
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            list.add(map(rs));
        }
        
    } catch (SQLException e) {
        throw new RuntimeException("khachhang findAll failed", e);
    }
    
    return list;
}
}