package flightbooking.dao;

import flightbooking.dto.GheDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GheDAO extends BaseDAO {

    private GheDTO map(ResultSet rs) throws SQLException {
    GheDTO g = new GheDTO();

    g.setGheId(rs.getInt("ghe_id"));

    int v = rs.getInt("maybay_id");
    g.setMayBayId(rs.wasNull() ? null : v);

    v = rs.getInt("hangghe_id");
    g.setHangGheId(rs.wasNull() ? null : v);

    v = rs.getInt("tang");                
    g.setTang(rs.wasNull() ? null : v);

    g.setTenGhe(rs.getString("tenghe"));

    int st = rs.getInt("trangthai");
    g.setTrangThai(rs.wasNull() ? null : st);

    return g;
}



    public GheDTO findById(int gheId) {
        String sql = "select * from ghe where ghe_id=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, gheId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ghe findById failed", e);
        }
    }

    public List<GheDTO> findByMayBay(int mayBayId) {
    String sql = "select * from ghe where maybay_id=? order by tang asc, tenghe asc";
    List<GheDTO> list = new ArrayList<>();
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, mayBayId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    } catch (SQLException e) {
        throw new RuntimeException("ghe findByMayBay failed", e);
    }
}
public List<GheDTO> findByMayBayAndTang(int mayBayId, int tang) {
    String sql = "select * from ghe where maybay_id=? and tang=? order by tenghe asc";
    List<GheDTO> list = new ArrayList<>();
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, mayBayId);
        ps.setInt(2, tang);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    } catch (SQLException e) {
        throw new RuntimeException("ghe findByMayBayAndTang failed", e);
    }
}
public int insert(GheDTO g) {
    String sql = "insert into ghe(maybay_id, hangghe_id, tang, tenghe, trangthai) values (?,?,?,?,?) returning ghe_id";
    try (Connection c = getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        if (g.getMayBayId() != null) ps.setInt(1, g.getMayBayId());
        else ps.setNull(1, Types.INTEGER);

        if (g.getHangGheId() != null) ps.setInt(2, g.getHangGheId());
        else ps.setNull(2, Types.INTEGER);

        if (g.getTang() != null) ps.setInt(3, g.getTang());
        else ps.setInt(3, 1);

        ps.setString(4, g.getTenGhe());

        if (g.getTrangThai() != null) ps.setInt(5, g.getTrangThai());
        else ps.setInt(5, 1);

        try (ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        throw new RuntimeException("ghe insert failed", e);
    }
}


}
