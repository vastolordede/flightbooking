package flightbooking.bus;

import flightbooking.dao.*;
import flightbooking.dto.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class DatVeBUS {

    private final TuyenBayDAO tuyenBayDAO = new TuyenBayDAO();
    private final ChuyenBayDAO chuyenBayDAO = new ChuyenBayDAO();
    private final GheDAO gheDAO = new GheDAO();
    private final VeDAO veDAO = new VeDAO();

    private final GiaHangChuyenBayDAO giaHangDAO = new GiaHangChuyenBayDAO();
    private final GiaGheOverrideDAO giaOverrideDAO = new GiaGheOverrideDAO();

    private final HanhKhachDAO hanhKhachDAO = new HanhKhachDAO();

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final HoaDonVeDAO hoaDonVeDAO = new HoaDonVeDAO();
    private final ThanhToanDAO thanhToanDAO = new ThanhToanDAO();

    // 1) TÌM CHUYẾN
    public List<ChuyenBayDTO> timChuyen(int sanBayDiId, int sanBayDenId, LocalDateTime tu, LocalDateTime den) {
        try {
            Integer tuyenBayId = tuyenBayDAO.findTuyenBayId(sanBayDiId, sanBayDenId);
            if (tuyenBayId == null) return Collections.emptyList();
            return chuyenBayDAO.findByTuyenBayAndTimeRange(tuyenBayId, tu, den);
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi tìm chuyến: " + ex.getMessage(), ex);
        }
    }

    // 2) GHẾ + TRẠNG THÁI GHẾ ĐÃ ĐẶT
    public List<GheDTO> dsGheCuaChuyen(int chuyenBayId) {
        try {
            ChuyenBayDTO cb = chuyenBayDAO.findById(chuyenBayId);
            if (cb == null) return Collections.emptyList();

            Integer mayBayId = cb.getMayBayId();
            if (mayBayId == null) return Collections.emptyList();

            List<GheDTO> all = gheDAO.findByMayBay(mayBayId);
            Set<Integer> daDat = new HashSet<>(veDAO.findGheIdDaDat(chuyenBayId));

            for (GheDTO g : all) {
                g.setDaDat(daDat.contains(g.getGheId()));
            }
            return all;
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi lấy danh sách ghế: " + ex.getMessage(), ex);
        }
    }

    // 3) TÍNH GIÁ 1 GHẾ
    public BigDecimal tinhGiaGhe(int chuyenBayId, int gheId) {
        GheDTO ghe = gheDAO.findById(gheId);
        if (ghe == null) throw new RuntimeException("Khong tim thay ghe_id=" + gheId);

        // override trước
        GiaGheOverrideDTO ov = giaOverrideDAO.findByChuyenBayAndGhe(chuyenBayId, gheId);
        if (ov != null) {
            BigDecimal gia = ov.getGiaOverride() != null ? ov.getGiaOverride() : BigDecimal.ZERO;
            BigDecimal thue = ov.getThuePhiOverride() != null ? ov.getThuePhiOverride() : BigDecimal.ZERO;
            return gia.add(thue);
        }

        // base theo hang ghe
        Integer hangGheId = ghe.getHangGheId();
        if (hangGheId == null) throw new RuntimeException("Ghe chua co hangghe_id, ghe_id=" + gheId);

        GiaHangChuyenBayDTO base = giaHangDAO.findByChuyenBayAndHangGhe(chuyenBayId, hangGheId);
        if (base == null) {
            throw new RuntimeException("Chua co gia base cho chuyenbay_id=" + chuyenBayId + ", hangghe_id=" + hangGheId);
        }

        BigDecimal gia = base.getGiaCoBan() != null ? base.getGiaCoBan() : BigDecimal.ZERO;
        BigDecimal thue = base.getThuePhi() != null ? base.getThuePhi() : BigDecimal.ZERO;
        return gia.add(thue);
    }

    // 4) ĐẶT VÉ
    public KetQuaDatVe datVe(
            Integer taiKhoanKhachHangId,
            Integer taiKhoanNhanVienId,
            int chuyenBayId,
            List<ThongTinHanhKhachVaGhe> items,
            String phuongThucThanhToan
    ) {
        if (items == null || items.isEmpty()) throw new RuntimeException("Danh sach hanh khach/ghe rong");

        // ====== STUB MODE: tạm thời không ghi DB (MOCK mua vé thành công) ======
        KetQuaDatVe kq = new KetQuaDatVe();
        kq.setHoaDonId(0);
        kq.setVeIds(new ArrayList<>());
        kq.setThanhToanId(0);
        kq.setTongTien(BigDecimal.ZERO);
        return kq;

        /*
        ===================== CODE DB THẬT (TẠM THỜI TẮT) =====================

        // 1) kiểm tra ghế trùng/đã đặt
        Set<Integer> unique = new HashSet<>();
        for (ThongTinHanhKhachVaGhe it : items) {
            if (!unique.add(it.getGheId())) throw new RuntimeException("Trung ghe_id=" + it.getGheId());
        }
        Set<Integer> daDat = new HashSet<>(veDAO.findGheIdDaDat(chuyenBayId));
        for (ThongTinHanhKhachVaGhe it : items) {
            if (daDat.contains(it.getGheId())) throw new RuntimeException("Ghe da duoc dat: ghe_id=" + it.getGheId());
        }

        // 2) tạo hóa đơn trước
        HoaDonDTO hd = new HoaDonDTO();
        hd.setTaiKhoanKhachHangId(taiKhoanKhachHangId);
        hd.setTaiKhoanNhanVienId(taiKhoanNhanVienId);
        hd.setTrangThai(1);

        int hoaDonId = hoaDonDAO.insert(hd);

        BigDecimal tong = BigDecimal.ZERO;
        List<Integer> veIds = new ArrayList<>();

        // 3) loop tạo hành khách + vé + liên kết hóa đơn
        for (ThongTinHanhKhachVaGhe it : items) {
            if (it.getHanhKhach() == null) throw new RuntimeException("Thong tin hanh khach rong");

            int hanhKhachId = hanhKhachDAO.insert(it.getHanhKhach());

            BigDecimal gia = tinhGiaGhe(chuyenBayId, it.getGheId());

            VeDTO ve = new VeDTO();
            ve.setChuyenBayId(chuyenBayId);
            ve.setGheId(it.getGheId());
            ve.setHanhKhachId(hanhKhachId);
            ve.setTaiKhoanNhanVienId(taiKhoanNhanVienId);
            ve.setGiaChot(gia);
            ve.setThueChot(BigDecimal.ZERO);
            ve.setTrangThai(1);

            int veId = veDAO.insert(ve);
            veIds.add(veId);

            HoaDonVeDTO hdv = new HoaDonVeDTO();
            hdv.setHoaDonId(hoaDonId);
            hdv.setVeId(veId);
            hoaDonVeDAO.insert(hdv);

            tong = tong.add(gia);
        }

        // 4) update tongtien
        hoaDonDAO.updateTongTien(hoaDonId, tong);

        // 5) thanh toán
        ThanhToanDTO tt = new ThanhToanDTO();
        tt.setHoaDonId(hoaDonId);
        tt.setSoTien(tong);
        tt.setPhuongThuc((phuongThucThanhToan == null || phuongThucThanhToan.trim().isEmpty())
                ? "cash"
                : phuongThucThanhToan.trim());
        tt.setTrangThai(1);

        int thanhToanId = thanhToanDAO.insert(tt);

        KetQuaDatVe kq2 = new KetQuaDatVe();
        kq2.setHoaDonId(hoaDonId);
        kq2.setVeIds(veIds);
        kq2.setThanhToanId(thanhToanId);
        kq2.setTongTien(tong);
        return kq2;

        =====================================================================
        */
    }

    // ====== DTO phụ cho BUS ======
    public static class ThongTinHanhKhachVaGhe {
        private HanhKhachDTO hanhKhach;
        private int gheId;

        public HanhKhachDTO getHanhKhach() { return hanhKhach; }
        public void setHanhKhach(HanhKhachDTO hanhKhach) { this.hanhKhach = hanhKhach; }

        public int getGheId() { return gheId; }
        public void setGheId(int gheId) { this.gheId = gheId; }
    }

    public static class KetQuaDatVe {
        private int hoaDonId;
        private List<Integer> veIds;
        private int thanhToanId;
        private BigDecimal tongTien;

        public int getHoaDonId() { return hoaDonId; }
        public void setHoaDonId(int hoaDonId) { this.hoaDonId = hoaDonId; }

        public List<Integer> getVeIds() { return veIds; }
        public void setVeIds(List<Integer> veIds) { this.veIds = veIds; }

        public int getThanhToanId() { return thanhToanId; }
        public void setThanhToanId(int thanhToanId) { this.thanhToanId = thanhToanId; }

        public BigDecimal getTongTien() { return tongTien; }
        public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    }
}
