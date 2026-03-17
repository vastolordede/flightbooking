package flightbooking.bus;

import flightbooking.dto.AccountDTO;
import flightbooking.dao.AccountDAO;

public class AccountBUS {
    private final AccountDAO accountDao = new AccountDAO();

    /**
     * Kiểm tra tính hợp lệ về mặt hình thức (Validation)
     */
    public String validateLogin(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return "Tên đăng nhập không được để trống.";
        }
        if (matKhau == null || matKhau.isEmpty()) {
            return "Mật khẩu không được để trống.";
        }
        if (matKhau.length() < 3) {
            return "Mật khẩu phải có ít nhất 3 ký tự.";
        }
        return null;
    }

    /**
     * Kiểm tra đăng nhập thật sự từ Database
     */
    public AccountDTO checkLogin(String tenDangNhap, String matKhau) {
        // Gọi DAO lấy tài khoản dựa trên TenDangNhap trong ERD
        AccountDTO account = accountDao.getAccountByUsername(tenDangNhap);

        // Kiểm tra: Tài khoản tồn tại + Mật khẩu khớp + Tài khoản đang hoạt động
        if (account != null 
            && account.getMatKhauMaHoa().equals(matKhau) 
            && account.isDangHoatDong()) {
            return account; 
        }
        
        return null; // Sai tên, sai mật khẩu hoặc tài khoản bị khóa
    }
    
    public AccountDTO checkLoginAdmin(String u, String p) {
    AccountDTO acc = checkLogin(u, p);
    // Giả sử NhomQuyenId = 1 là Admin/NhanVien
    if (acc != null && acc.getNhomQuyenId() == 1) {
        return acc;
    }
    return null; // Trả về null nếu là khách hàng hoặc sai pass
}
    
    
    public String registerAccount(AccountDTO account, String confirmPass) {
    // 1. Kiểm tra mật khẩu nhập lại
    if (!account.getMatKhauMaHoa().equals(confirmPass)) {
        return "Mật khẩu xác nhận không khớp!";
    }
    
    // 2. Kiểm tra tên đăng nhập đã tồn tại trong DB chưa
    if (accountDao.getAccountByUsername(account.getTenDangNhap()) != null) {
        return "Tên đăng nhập này đã tồn tại, vui lòng chọn tên khác!";
    }
    
    // 3. Gọi DAO để lưu
    boolean success = accountDao.insertAccount(account);
    return success ? null : "Lỗi hệ thống: Không thể tạo tài khoản lúc này.";
}
}