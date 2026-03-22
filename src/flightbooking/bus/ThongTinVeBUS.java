package flightbooking.bus;

import flightbooking.dao.ThongTinVeDAO;
import flightbooking.dto.ThongTinVeDTO;

public class ThongTinVeBUS {

    private final ThongTinVeDAO dao = new ThongTinVeDAO();

    public ThongTinVeDTO getFullInfo(int chuyenBayId, int gheId, String hoTen, String soGiayTo) {

        ThongTinVeDTO t = dao.getFullInfo(chuyenBayId, gheId);

        if (t != null) {
            t.setHoTen(hoTen);
            t.setSoGiayTo(soGiayTo);
        }

        return t;
    }
}