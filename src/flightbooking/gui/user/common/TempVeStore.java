package flightbooking.gui.user.common;

import flightbooking.bus.DatVeBUS;
import java.util.ArrayList;
import java.util.List;

public class TempVeStore {

    public static List<DatVeBUS.ThongTinHanhKhachVaGhe> DS = new ArrayList<>();

    public static void clear() {
        DS.clear();
    }

    public static void add(DatVeBUS.ThongTinHanhKhachVaGhe item) {
        DS.add(item);
    }

    public static List<DatVeBUS.ThongTinHanhKhachVaGhe> getAll() {
        return DS;
    }
}