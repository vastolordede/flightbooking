package flightbooking.dto;

import java.time.LocalDateTime;

public class ThongTinVeDTO {

    private int chuyenBayId;
    private String sanBayDi;
    private String sanBayDen;
    private LocalDateTime gioKhoiHanh;
    private LocalDateTime gioDen;

    private String tenMayBay;
    private String kieuMayBay;

    private String tenGhe;
    private String hangGhe;

    private String hoTen;
    private String soGiayTo;

    public int getChuyenBayId() { return chuyenBayId; }
    public void setChuyenBayId(int chuyenBayId) { this.chuyenBayId = chuyenBayId; }

    public String getSanBayDi() { return sanBayDi; }
    public void setSanBayDi(String sanBayDi) { this.sanBayDi = sanBayDi; }

    public String getSanBayDen() { return sanBayDen; }
    public void setSanBayDen(String sanBayDen) { this.sanBayDen = sanBayDen; }

    public LocalDateTime getGioKhoiHanh() { return gioKhoiHanh; }
    public void setGioKhoiHanh(LocalDateTime gioKhoiHanh) { this.gioKhoiHanh = gioKhoiHanh; }

    public LocalDateTime getGioDen() { return gioDen; }
    public void setGioDen(LocalDateTime gioDen) { this.gioDen = gioDen; }

    public String getTenMayBay() { return tenMayBay; }
    public void setTenMayBay(String tenMayBay) { this.tenMayBay = tenMayBay; }

    public String getKieuMayBay() { return kieuMayBay; }
    public void setKieuMayBay(String kieuMayBay) { this.kieuMayBay = kieuMayBay; }

    public String getTenGhe() { return tenGhe; }
    public void setTenGhe(String tenGhe) { this.tenGhe = tenGhe; }

    public String getHangGhe() { return hangGhe; }
    public void setHangGhe(String hangGhe) { this.hangGhe = hangGhe; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoGiayTo() { return soGiayTo; }
    public void setSoGiayTo(String soGiayTo) { this.soGiayTo = soGiayTo; }
}