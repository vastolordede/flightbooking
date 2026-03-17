package flightbooking.bus;

import flightbooking.dao.ChucVuDAO;
import flightbooking.dao.NhanVienDAO;
import flightbooking.dao.PhongBanDAO;
import flightbooking.dto.NhanVienDTO;
import java.math.BigDecimal;
import java.util.List;

public class NhanVienBUS {

    private final PhongBanDAO phongBanDAO = new PhongBanDAO();
    private final ChucVuDAO chucVuDAO = new ChucVuDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    // ====================== LẤY DANH SÁCH ======================
    public List<NhanVienDTO> layDanhSach() {
        return nhanVienDAO.findAll();
    }

    public NhanVienDTO findById(int id) {
        if (id <= 0)
            throw new RuntimeException("ID không hợp lệ!");

        NhanVienDTO nv = nhanVienDAO.findById(id);

        if (nv == null)
            throw new RuntimeException("Nhân viên không tồn tại!");

        return nv;
    }

    public List<NhanVienDTO> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty())
            return layDanhSach();

        return nhanVienDAO.findByHoTen(keyword.trim());
    }

    // ====================== THÊM ======================
    public void themNhanVien(NhanVienDTO nv) {

        validateNhanVien(nv, false);

        nhanVienDAO.insert(nv);
    }

    // ====================== SỬA ======================
    public void suaNhanVien(NhanVienDTO nv) {

        validateNhanVien(nv, true);

        // Kiểm tra tồn tại trước khi update
        if (nhanVienDAO.findById(nv.getNhanVienId()) == null)
            throw new RuntimeException("Nhân viên không tồn tại!");

        nhanVienDAO.update(nv);
    }

    // ====================== XOÁ MỀM ======================
    public void xoaNhanVien(int id) {

        if (id <= 0)
            throw new RuntimeException("ID không hợp lệ!");

        if (nhanVienDAO.findById(id) == null)
            throw new RuntimeException("Nhân viên không tồn tại!");

        nhanVienDAO.softDelete(id);
    }

    // ====================== VALIDATION CHUNG ======================
    private void validateNhanVien(NhanVienDTO nv, boolean isUpdate) {

        if (nv == null)
            throw new RuntimeException("Dữ liệu không hợp lệ!");

        // Nếu update phải có ID
        if (isUpdate) {
            if (nv.getNhanVienId() <= 0)
                throw new RuntimeException("ID không hợp lệ!");
        }

        // ===== Họ tên =====
        if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty())
            throw new RuntimeException("Họ tên không được để trống!");

        nv.setHoTen(nv.getHoTen().trim());

        // ===== Số điện thoại =====
        if (nv.getDienThoai() == null || nv.getDienThoai().trim().isEmpty())
            throw new RuntimeException("Số điện thoại không được để trống!");

        if (!nv.getDienThoai().matches("\\d{9,11}"))
            throw new RuntimeException("Số điện thoại phải từ 9 đến 11 chữ số!");

        // ===== Email =====
        if (nv.getEmail() == null || nv.getEmail().trim().isEmpty())
            throw new RuntimeException("Email không được để trống!");

        if (!nv.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new RuntimeException("Email không hợp lệ!");
        
        nv.setEmail(nv.getEmail().trim().toLowerCase());

        // ===== Lương =====
        if (nv.getLuongCoBan() == null ||
            nv.getLuongCoBan().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Lương phải lớn hơn 0!");

        // ===== Ngày vào làm =====
        if (nv.getNgayVaoLam() == null)
            throw new RuntimeException("Ngày vào làm không được để trống!");

        // ===== Phòng ban =====
        if (nv.getPhongBanId() != null) {
            if (nv.getPhongBanId() <= 0)
                throw new RuntimeException("Phòng ban không hợp lệ!");

            if (phongBanDAO.findById(nv.getPhongBanId()) == null)
                throw new RuntimeException("Phòng ban không tồn tại!");
        }

        // ===== Chức vụ =====
        if (nv.getChucVuId() != null) {
            if (nv.getChucVuId() <= 0)
                throw new RuntimeException("Chức vụ không hợp lệ!");

            if (chucVuDAO.findById(nv.getChucVuId()) == null)
                throw new RuntimeException("Chức vụ không tồn tại!");
        }

        // ===== Trạng thái =====
        if (nv.getTrangThai() != 0 && nv.getTrangThai() != 1) {
            if (!isUpdate) {
                nv.setTrangThai(1); // thêm mới thì mặc định = 1
            } else {
                throw new RuntimeException("Trạng thái chỉ được 0 hoặc 1!");
            }
        }
    }
}