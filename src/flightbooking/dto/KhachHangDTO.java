package flightbooking.dto;

/**
 * Lớp chứa thông tin khách hàng - Khớp với bảng hanhkhach và taikhoankhachhang trên Neon
 */
public class KhachHangDTO {
    private int hanhkhachId;
    private String hoTen;
    private String dienThoai;
    private String tenDangNhap;
    private String matKhauMaHoa;
    private boolean dangHoatDong;

    // Constructor không tham số
    public KhachHangDTO() {
    }

    // Constructor đầy đủ tham số
    public KhachHangDTO(int hanhkhachId, String hoTen, String dienThoai, String tenDangNhap, String matKhauMaHoa, boolean dangHoatDong) {
        this.hanhkhachId = hanhkhachId;
        this.hoTen = hoTen;
        this.dienThoai = dienThoai;
        this.tenDangNhap = tenDangNhap;
        this.matKhauMaHoa = matKhauMaHoa;
        this.dangHoatDong = dangHoatDong;
    }

    // Getter và Setter
    public int getHanhkhachId() { return hanhkhachId; }
    public void setHanhkhachId(int hanhkhachId) { this.hanhkhachId = hanhkhachId; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhauMaHoa() { return matKhauMaHoa; }
    public void setMatKhauMaHoa(String matKhauMaHoa) { this.matKhauMaHoa = matKhauMaHoa; }

    public boolean isDangHoatDong() { return dangHoatDong; }
    public void setDangHoatDong(boolean dangHoatDong) { this.dangHoatDong = dangHoatDong; }
}