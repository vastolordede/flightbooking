package flightbooking.dao;

import flightbooking.dto.KhachHangDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO extends BaseDAO {

    // ================= MAP RESULTSET =================
    private KhachHangDTO map(ResultSet rs) throws SQLException {
    KhachHangDTO kh = new KhachHangDTO();

    // ❌ KHÔNG còn hanhkhach_id trong query
    kh.setKhachHangId(0); // hoặc -1

    // ❌ KHÔNG có hoten
    kh.setHoTen(""); // hoặc null

    // ✔️ có trong bảng
    kh.setDienThoai(rs.getString("dienthoai"));
    kh.setTenDangNhap(rs.getString("email"));
    kh.setMatKhauMaHoa(rs.getString("matkhau_mahoa"));
    kh.setDangHoatDong(rs.getInt("trangthai") == 1);

    return kh;
}

    // ================= CHECK USER EXISTS =================
    public boolean existsByUsername(String email) {
        String sql = "SELECT 1 FROM taikhoankhachhang WHERE LOWER(email) = LOWER(?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("khachhang existsByUsername failed", e);
        }
    }

    // ================= REGISTER =================
    public boolean register(String hoTen, String sdt, String email, String pass) {

        if (existsByUsername(email)) return false;

        String sqlInsertKH =
                "INSERT INTO hanhkhach(hoten, dienthoai) VALUES(?, ?) RETURNING hanhkhach_id";

        String sqlInsertTK =
                "INSERT INTO taikhoankhachhang(email, dienthoai, matkhau_mahoa, trangthai) VALUES(?, ?, ?, ?)";

        try (Connection c = getConnection()) {
            c.setAutoCommit(false);

            int hanhkhachId = -1;

            // 1. insert hanhkhach
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

            // 2. insert taikhoan (KHÔNG dùng hanhkhach_id vì DB bạn không có FK)
            try (PreparedStatement ps = c.prepareStatement(sqlInsertTK)) {

                ps.setString(1, email.trim());
                ps.setString(2, sdt.trim());
                ps.setString(3, pass);
                ps.setInt(4, 1); // trangthai = 1

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

    // ================= LOGIN =================
    public boolean checkLogin(String email, String pass) {

        String sql = "SELECT 1 FROM taikhoankhachhang " +
                "WHERE LOWER(email) = LOWER(?) " +
                "AND matkhau_mahoa = ? " +
                "AND trangthai = 1";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("khachhang checkLogin failed", e);
        }
    }

    // ================= FIND BY EMAIL =================
    public KhachHangDTO findByUsername(String email) {

    String sql =
        "SELECT email, dienthoai, matkhau_mahoa, trangthai " +
        "FROM taikhoankhachhang " +
        "WHERE LOWER(email) = LOWER(?)";

    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, email.trim());

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return map(rs); // ✅ giờ không crash nữa
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("khachhang findByUsername failed", e);
    }

    return null;
}

    // ================= FIND BY ID =================
    public KhachHangDTO findById(int hanhkhachId) {

        String sql =
                "SELECT kh.hanhkhach_id, kh.hoten, kh.dienthoai, " +
                "tk.email, tk.matkhau_mahoa, tk.trangthai " +
                "FROM hanhkhach kh " +
                "JOIN taikhoankhachhang tk ON tk.dienthoai = kh.dienthoai " +
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

    // ================= FIND ALL =================
    public List<KhachHangDTO> findAll() {

        String sql =
                "SELECT kh.hanhkhach_id, kh.hoten, kh.dienthoai, " +
                "tk.email, tk.matkhau_mahoa, tk.trangthai " +
                "FROM hanhkhach kh " +
                "JOIN taikhoankhachhang tk ON tk.dienthoai = kh.dienthoai " +
                "WHERE tk.trangthai = 1 " +
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