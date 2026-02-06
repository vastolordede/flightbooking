package flightbooking.bus;

import flightbooking.dao.GheDAO;
import flightbooking.dto.GheDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GheGeneratorBUS {

    private final GheDAO gheDAO = new GheDAO();

    // format: 1.E.A1 (tầng.prefix.rowcol)
    public void taoGheTheoHang(
            int mayBayId,
            int hangGheId,
            String tenHangInput,
            int soTang,
            int soHang,
            int soCot
    ) {
        if (mayBayId <= 0) throw new RuntimeException("mayBayId không hợp lệ");
        if (hangGheId <= 0) throw new RuntimeException("hangGheId không hợp lệ");
        if (tenHangInput == null || tenHangInput.trim().isEmpty())
            throw new RuntimeException("Tên hạng không được rỗng");

        if (soTang <= 0) soTang = 1;
        if (soHang <= 0) throw new RuntimeException("Số hàng phải > 0");
        if (soCot <= 0) throw new RuntimeException("Số cột phải > 0");

        String prefix = tenHangInput.trim().substring(0, 1).toUpperCase(); // E/B/F...

        // ✅ chặn generate trùng: load tất cả ghế hiện có của máy bay
        List<GheDTO> existed = gheDAO.findByMayBay(mayBayId);
        Set<String> existedNames = new HashSet<>();
        for (GheDTO g : existed) {
            if (g.getTenGhe() != null) existedNames.add(g.getTenGhe().trim().toUpperCase());
        }

        for (int tang = 1; tang <= soTang; tang++) {
            for (int r = 0; r < soHang; r++) {
                char rowChar = (char) ('A' + r);
                for (int c = 1; c <= soCot; c++) {

                    String tenGhe = tang + "." + prefix + "." + rowChar + c; // 1.E.A1

                    // ✅ nếu đã tồn tại -> skip (hoặc throw tuỳ bạn)
                    if (existedNames.contains(tenGhe.toUpperCase())) {
                        continue;
                    }

                    GheDTO g = new GheDTO();
                    g.setMayBayId(mayBayId);
                    g.setHangGheId(hangGheId);
                    g.setTang(tang);
                    g.setTenGhe(tenGhe);
                    g.setTrangThai(1);

                    gheDAO.insert(g);

                    existedNames.add(tenGhe.toUpperCase()); // tránh trùng ngay trong lần generate này
                }
            }
        }
    }
}
