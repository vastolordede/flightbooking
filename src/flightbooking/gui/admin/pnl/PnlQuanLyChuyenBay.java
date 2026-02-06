package flightbooking.gui.admin.pnl;

import flightbooking.bus.ChuyenBayBUS;
import flightbooking.bus.MayBayBUS;
import flightbooking.bus.TuyenBayBUS;
import flightbooking.dto.ChuyenBayDTO;
import flightbooking.dto.MayBayDTO;
import flightbooking.dto.TuyenBayDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PnlQuanLyChuyenBay extends JPanel {

    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();
    private final TuyenBayBUS tuyenBayBUS = new TuyenBayBUS();
    private final MayBayBUS mayBayBUS = new MayBayBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "TuyenBayId", "Tuyến", "HHK_ID", "Máy bay", "Giờ KH", "Giờ Đến", "Trạng thái"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private final JComboBox<Item> cbTuyenBay = new JComboBox<>();
    private final JTextField txtHangHangKhongId = new JTextField();

    // ✅ đổi sang combo máy bay
    private final JComboBox<MayBayItem> cbMayBay = new JComboBox<>();

    private final JTextField txtGioKhoiHanh = new JTextField();
    private final JTextField txtGioDen = new JTextField();
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"1 - Đang mở", "0 - Hủy/Đóng"});

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PnlQuanLyChuyenBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTuyenBayToCombo();
        loadMayBayToCombo();
        reload();

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());

        // Ẩn cột TuyenBayId (index=1)
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));

        form.add(new JLabel("Tuyến bay"));
        form.add(cbTuyenBay);

        form.add(new JLabel("Hãng HK ID"));
        form.add(txtHangHangKhongId);

        form.add(new JLabel("Máy bay"));
        form.add(cbMayBay);

        form.add(new JLabel("Giờ khởi hành (yyyy-MM-dd HH:mm)"));
        form.add(txtGioKhoiHanh);

        form.add(new JLabel("Giờ đến (yyyy-MM-dd HH:mm)"));
        form.add(txtGioDen);

        form.add(new JLabel("Trạng thái"));
        form.add(cbTrangThai);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);

        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private void loadTuyenBayToCombo() {
        cbTuyenBay.removeAllItems();
        List<TuyenBayDTO> list = tuyenBayBUS.dsTuyenBay();
        for (TuyenBayDTO t : list) {
            String di = t.getSanBayDiTen() != null ? t.getSanBayDiTen() : ("SB#" + t.getSanBayDiId());
            String den = t.getSanBayDenTen() != null ? t.getSanBayDenTen() : ("SB#" + t.getSanBayDenId());
            cbTuyenBay.addItem(new Item(t.getTuyenBayId(), di + " → " + den));
        }
    }

    private void loadMayBayToCombo() {
        cbMayBay.removeAllItems();
        List<MayBayDTO> list = mayBayBUS.dsMayBay();
        for (MayBayDTO m : list) {
            String text = m.getTenMayBay() + " • " + m.getKieuMayBay()
                    + " • tầng=" + (m.getSoTang() == null ? 1 : m.getSoTang())
                    + " • ID=" + m.getMayBayId();
            cbMayBay.addItem(new MayBayItem(m.getMayBayId(), text));
        }
    }

    private void reload() {
        model.setRowCount(0);
        List<ChuyenBayDTO> list = chuyenBayBUS.dsChuyenBay();

        for (ChuyenBayDTO c : list) {
            String tuyenTxt = "Tuyến " + c.getTuyenBayId();
            String mbTxt = (c.getMayBayId() != null) ? ("MB#" + c.getMayBayId()) : "(null)";

            model.addRow(new Object[]{
                    c.getChuyenBayId(),
                    c.getTuyenBayId(),
                    tuyenTxt,
                    c.getHangHangKhongId(),
                    mbTxt,
                    c.getGioKhoiHanh(),
                    c.getGioDen(),
                    c.getTrangThai()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Integer tuyenBayId = (Integer) model.getValueAt(row, 1);
        Integer hhk = (Integer) model.getValueAt(row, 3);

        // vì bảng đang hiển thị "MB#id" nên ta lấy lại từ BUS theo id ở DTO tốt nhất,
        // nhưng hiện model không lưu mayBayId riêng -> dùng lại dsChuyenBay để map theo id nhanh nhất.
        int cbId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        ChuyenBayDTO dto = chuyenBayBUS.findById(cbId); // ✅ nếu bạn có. Nếu chưa có thì bảo mình, mình viết luôn.
        Integer mbId = dto != null ? dto.getMayBayId() : null;

        Object gkh = model.getValueAt(row, 5);
        Object gd = model.getValueAt(row, 6);
        Integer tt = (Integer) model.getValueAt(row, 7);

        selectComboById(cbTuyenBay, tuyenBayId != null ? tuyenBayId : -1);
        txtHangHangKhongId.setText(hhk == null ? "" : String.valueOf(hhk));
        selectComboMayBayById(mbId != null ? mbId : -1);

        txtGioKhoiHanh.setText(gkh == null ? "" : String.valueOf(gkh).replace('T', ' '));
        txtGioDen.setText(gd == null ? "" : String.valueOf(gd).replace('T', ' '));

        cbTrangThai.setSelectedIndex(tt != null && tt == 1 ? 0 : 1);
    }

    private void selectComboById(JComboBox<Item> cb, int id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            Item it = cb.getItemAt(i);
            if (it != null && it.id == id) { cb.setSelectedIndex(i); return; }
        }
    }

    private void selectComboMayBayById(int id) {
        for (int i = 0; i < cbMayBay.getItemCount(); i++) {
            MayBayItem it = cbMayBay.getItemAt(i);
            if (it != null && it.id == id) { cbMayBay.setSelectedIndex(i); return; }
        }
    }

    private void add() {
        Item tuyen = (Item) cbTuyenBay.getSelectedItem();
        MayBayItem mb = (MayBayItem) cbMayBay.getSelectedItem();
        if (tuyen == null || mb == null) return;

        ChuyenBayDTO c = new ChuyenBayDTO();
        c.setTuyenBayId(tuyen.id);
        c.setHangHangKhongId(parseInt(txtHangHangKhongId.getText(), "Hãng HK ID"));
        c.setMayBayId(mb.id);

        LocalDateTime gkh = parseDate(txtGioKhoiHanh.getText().trim(), "Giờ khởi hành");
        LocalDateTime gd = parseDate(txtGioDen.getText().trim(), "Giờ đến");
        c.setGioKhoiHanh(gkh);
        c.setGioDen(gd);
        c.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);

        chuyenBayBUS.themChuyenBay(c);
        reload();
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Item tuyen = (Item) cbTuyenBay.getSelectedItem();
        MayBayItem mb = (MayBayItem) cbMayBay.getSelectedItem();
        if (tuyen == null || mb == null) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        ChuyenBayDTO c = new ChuyenBayDTO();
        c.setChuyenBayId(id);
        c.setTuyenBayId(tuyen.id);
        c.setHangHangKhongId(parseInt(txtHangHangKhongId.getText(), "Hãng HK ID"));
        c.setMayBayId(mb.id);

        c.setGioKhoiHanh(parseDate(txtGioKhoiHanh.getText().trim(), "Giờ khởi hành"));
        c.setGioDen(parseDate(txtGioDen.getText().trim(), "Giờ đến"));
        c.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);

        chuyenBayBUS.capNhatChuyenBay(c);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        chuyenBayBUS.xoaChuyenBay(id);
        reload();
    }

    private int parseInt(String s, String fieldName) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Trường " + fieldName + " phải là số.");
            throw e;
        }
    }

    private LocalDateTime parseDate(String s, String fieldName) {
        try { return LocalDateTime.parse(s, fmt); }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Sai định dạng " + fieldName + ". Ví dụ: 2026-02-05 14:30");
            throw e;
        }
    }

    private static class Item {
        final int id; final String text;
        Item(int id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text + " (ID=" + id + ")"; }
    }

    private static class MayBayItem {
        final int id; final String text;
        MayBayItem(int id, String text) { this.id = id; this.text = text; }
        @Override public String toString() { return text; }
    }
}
