package flightbooking.bus;

import flightbooking.dao.GheDAO;
import flightbooking.dto.GheDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GheGeneratorBUS {

    private final GheDAO gheDAO = new GheDAO();

    // format: 1.E.A1 (tầng.prefix.rowcol)
    // Bỏ biến soTang, thêm biến rowOffset
public void taoGheTheoHang(
        int mayBayId,
        int hangGheId,
        String tenHangInput,
        int soHang,
        int soCot,
        int rowOffset // Biến này để biết bắt đầu từ chữ cái nào
) {
    if (mayBayId <= 0) throw new RuntimeException("mayBayId không hợp lệ");
    if (hangGheId <= 0) throw new RuntimeException("hangGheId không hợp lệ");
    if (tenHangInput == null || tenHangInput.trim().isEmpty())
        throw new RuntimeException("Tên hạng không được rỗng");
    if (soHang <= 0) throw new RuntimeException("Số hàng phải > 0");
    if (soCot <= 0) throw new RuntimeException("Số cột phải > 0");

    String prefix = tenHangInput.trim().substring(0, 1).toUpperCase(); 

    List<GheDTO> existed = gheDAO.findByMayBay(mayBayId);
    Set<String> existedNames = new HashSet<>();
    for (GheDTO g : existed) {
        if (g.getTenGhe() != null) existedNames.add(g.getTenGhe().trim().toUpperCase());
    }

    // Bỏ vòng lặp Tầng. Chỉ còn lặp Hàng và Cột
    for (int r = 0; r < soHang; r++) {
        // Cộng thêm rowOffset để ra chữ cái tiếp theo. 
        // Ví dụ offset=0, r=0 -> 'A'. offset=5 (đã tạo 5 hàng Eco), r=0 -> 'F'
        char rowChar = (char) ('A' + r + rowOffset); 
        
        for (int c = 1; c <= soCot; c++) {
            // Format mới: Hạng.HàngCột (VD: E.A1). Bỏ số 1 ở đầu (Tầng)
            String tenGhe = prefix + "." + rowChar + c; 

            if (existedNames.contains(tenGhe.toUpperCase())) {
                continue;
            }

            GheDTO g = new GheDTO();
            g.setMayBayId(mayBayId);
            g.setHangGheId(hangGheId);
            // g.setTang(1); // Xóa dòng này nếu DB của bạn đã xóa cột Tang. Nếu DB vẫn còn, set cứng = 1.
            g.setTenGhe(tenGhe);
            g.setTrangThai(1);

            gheDAO.insert(g);
            existedNames.add(tenGhe.toUpperCase()); 
        }
    }
}
}
