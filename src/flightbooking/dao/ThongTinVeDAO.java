package flightbooking.dao;

import flightbooking.dto.ThongTinVeDTO;

import java.sql.*;

public class ThongTinVeDAO extends BaseDAO {

    public ThongTinVeDTO getFullInfo(int chuyenBayId, int gheId) {

        String sql =
    "SELECT cb.chuyenbay_id, cb.giokhoihanh, cb.gioden, " +
    "sbd.tensanbay AS sanbay_di, sbdn.tensanbay AS sanbay_den, " +
    "mb.tenmaybay, mb.kieumaybay, " +
    "g.tenghe, hgm.tenhangghe " +
    "FROM chuyenbay cb " +
    "JOIN tuyenbay tb ON tb.tuyenbay_id = cb.tuyenbay_id " +
    "JOIN sanbay sbd ON sbd.sanbay_id = tb.sanbaydi_id " +
    "JOIN sanbay sbdn ON sbdn.sanbay_id = tb.sanbayden_id " +
    "JOIN maybay mb ON mb.maybay_id = cb.maybay_id " +
    "JOIN ghe g ON g.ghe_id = ? " +
    "LEFT JOIN hangghemaybay hgm ON hgm.hangghe_id = g.hangghe_id " +
    "WHERE cb.chuyenbay_id = ?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, gheId);
            ps.setInt(2, chuyenBayId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return null;

                ThongTinVeDTO t = new ThongTinVeDTO();

                t.setChuyenBayId(rs.getInt("chuyenbay_id"));
                t.setSanBayDi(rs.getString("sanbay_di"));
                t.setSanBayDen(rs.getString("sanbay_den"));
                t.setGioKhoiHanh(rs.getTimestamp("giokhoihanh").toLocalDateTime());
                t.setGioDen(rs.getTimestamp("gioden").toLocalDateTime());

                t.setTenMayBay(rs.getString("tenmaybay"));
                t.setKieuMayBay(rs.getString("kieumaybay"));

                t.setTenGhe(rs.getString("tenghe"));
                t.setHangGhe(rs.getString("tenhangghe"));

                return t;
            }

        } catch (SQLException e) {
            throw new RuntimeException("getFullInfo failed", e);
        }
    }
}