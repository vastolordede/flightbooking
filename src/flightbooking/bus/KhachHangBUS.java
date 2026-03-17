package flightbooking.bus;

import flightbooking.dao.KhachHangDAO;
import flightbooking.util.UiUtil;
import java.awt.Component;
import flightbooking.dto.KhachHangDTO;



public class KhachHangBUS {
    // 1. Phải khởi tạo DAO để sử dụng, nếu không sẽ bị lỗi NullPointerException
    private final KhachHangDAO khDAO = new KhachHangDAO();

    /**
     * Xử lý logic Đăng ký tài khoản khách hàng
     */
    
    public KhachHangDTO getByUsername(String tenDangNhap) {
        return khDAO.findByUsername(tenDangNhap);
    }

    /**
     * Lấy thông tin khách hàng từ ID
     */
    public KhachHangDTO getById(int hanhkhachId) {
        return khDAO.findById(hanhkhachId);
    }
    
    public boolean dangKy(Component parent, String hoTen, String sdt, String user, String pass, String rePass) {
        // Kiểm tra rỗng
        if (hoTen.isEmpty() || sdt.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            UiUtil.showInfo(parent, "Vui lòng nhập đầy đủ tất cả thông tin!");
            return false;
        }

        // Kiểm tra khớp mật khẩu
        if (!pass.equals(rePass)) {
            UiUtil.showInfo(parent, "Mật khẩu xác nhận không trùng khớp!");
            return false;
        }

        // Kiểm tra độ dài mật khẩu (tùy chọn nhưng nên có)
        if (pass.length() < 3) {
            UiUtil.showInfo(parent, "Mật khẩu phải có ít nhất 3 ký tự!");
            return false;
        }

        // Gọi xuống DAO để thực hiện Insert vào Neon
        if (khDAO.register(hoTen, sdt, user, pass)) {
            UiUtil.showInfo(parent, "Chúc mừng! Bạn đã đăng ký tài khoản thành công.");
            return true;
        } else {
            UiUtil.showInfo(parent, "Đăng ký thất bại! Tên đăng nhập có thể đã tồn tại.");
            return false;
        }
    }

    /**
     * Xử lý logic Đăng nhập khách hàng
     */
    public boolean dangNhap(Component parent, String user, String pass) {
        if (user.isEmpty() || pass.isEmpty()) {
            UiUtil.showInfo(parent, "Tên đăng nhập và mật khẩu không được để trống!");
            return false;
        }

        // Kiểm tra thông tin trong bảng taikhoankhachhang
        if (khDAO.checkLogin(user, pass)) {
            return true; // Thành công
        } else {
            UiUtil.showInfo(parent, "Tài khoản hoặc mật khẩu không chính xác!");
            return false;
        }
    }
}