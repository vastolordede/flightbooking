package flightbooking.dao;

import flightbooking.dto.HanhKhachDTO;

import java.sql.*;

public class HanhKhachDAO extends BaseDAO {

    public int insert(HanhKhachDTO hk) {
        String sql = "insert into hanhkhach(hoten, ngaysinh, sogiayto, quoctich) values (?,?,?,?) returning hanhkhach_id";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hk.getHoTen());

if (hk.getNgaySinh() != null)
    ps.setDate(2, Date.valueOf(hk.getNgaySinh()));
else
    ps.setNull(2, Types.DATE);

if (hk.getSoGiayTo() != null && !hk.getSoGiayTo().trim().isEmpty())
    ps.setString(3, hk.getSoGiayTo().trim());
else
    ps.setNull(3, Types.VARCHAR);

if (hk.getQuocTich() != null && !hk.getQuocTich().trim().isEmpty())
    ps.setString(4, hk.getQuocTich().trim());
else
    ps.setNull(4, Types.VARCHAR);


            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("hanhkhach insert failed", e);
        }
    }
}
