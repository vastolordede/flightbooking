package flightbooking.dao;

import flightbooking.dto.GiaHangChuyenBayDTO;

import java.sql.*;

public class GiaHangChuyenBayDAO extends BaseDAO {

    public GiaHangChuyenBayDTO findByChuyenBayAndHangGhe(int chuyenBayId, int hangGheId) {
        String sql = "select * from giahchuyenbay where chuyenbay_id=? and hangghe_id=? limit 1";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, chuyenBayId);
            ps.setInt(2, hangGheId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

               GiaHangChuyenBayDTO d = new GiaHangChuyenBayDTO();

d.setGiaHChuyenBayId(rs.getInt("giahchuyenbay_id"));

int v = rs.getInt("chuyenbay_id");
d.setChuyenBayId(rs.wasNull() ? null : v);

v = rs.getInt("hangghe_id");
d.setHangGheId(rs.wasNull() ? null : v);

d.setGiaCoBan(rs.getBigDecimal("giacoban"));
d.setThuePhi(rs.getBigDecimal("thuephi"));

Timestamp t = rs.getTimestamp("capnhatluc");
if (t != null) d.setCapNhatLuc(t.toLocalDateTime());

                return d;
            }
        } catch (SQLException e) {
            throw new RuntimeException("giahchuyenbay findByChuyenBayAndHangGhe failed", e);
        }
    }
}
