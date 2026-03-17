package flightbooking.bus;

import flightbooking.dao.GheDAO;
import flightbooking.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GheGeneratorBUS {

    private final GheDAO gheDAO = new GheDAO();

    public void taoGheTheoHang(
        int mayBayId,
        int hangGheId,
        String tenHang,
        int soGhe,
        int soHang,
        int rowOffset
) {
    int soCot = (int) Math.ceil((double) soGhe / soHang);
    int seatCreated = 0;

    String sql =
        "INSERT INTO ghe (maybay_id, hangghe_id, tang, row_index, col_index, tenghe, trangthai) " +
        "VALUES (?,?,?,?,?,?,?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        conn.setAutoCommit(false);

        for (int r = 0; r < soHang; r++) {
            int rowIndex = rowOffset + r + 1; // Số hàng (1, 2, 3...)

            for (int c = 1; c <= soCot; c++) {
                if (seatCreated >= soGhe) break;

                // Tạo tên ghế theo chuẩn mới: Số hàng + Chữ cột (Ví dụ: 1A, 1B)
                char colLetter = (char) ('A' + c - 1);
                String tenGheMoi = rowIndex + "" + colLetter;

                ps.setInt(1, mayBayId);
                ps.setInt(2, hangGheId);
                ps.setInt(3, 1);
                ps.setInt(4, rowIndex); // Lưu số vào rowindex
                ps.setInt(5, c);        // Lưu số vào colindex
                ps.setString(6, tenGheMoi); 
                ps.setInt(7, 1);

                ps.addBatch();
                seatCreated++;
            }
        }

        ps.executeBatch();
        conn.commit();
        System.out.println("Đã tạo thành công " + seatCreated + " ghế cho hạng " + tenHang);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}