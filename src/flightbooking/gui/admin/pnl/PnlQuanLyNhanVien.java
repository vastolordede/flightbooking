package flightbooking.gui.admin.pnl;

import flightbooking.bus.ChucVuBUS;
import flightbooking.bus.NhanVienBUS;
import flightbooking.bus.PhongBanBUS;
import flightbooking.dao.QuyenDAO;
import flightbooking.dto.ChucVuDTO;
import flightbooking.dto.NhanVienDTO;
import flightbooking.dto.PhongBanDTO;
import flightbooking.dto.QuyenDTO;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PnlQuanLyNhanVien extends JPanel {

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final PhongBanBUS phongBanBUS = new PhongBanBUS();
    private final ChucVuBUS chucVuBUS = new ChucVuBUS();
    // private final QuyenDAO quyenDAO = new QuyenDAO();

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[]{
                "ID",
                "Họ tên",
                "SĐT",
                "Email",
                "Phòng ban",
                "Chức vụ",
                "Ngày vào làm",
                "Trạng thái",
                "Ngày nghỉ",
                "Lương cơ bản"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtDienThoai = new JTextField();
    private final JTextField txtLuong = new JTextField();
    private final JTextField txtNgayVaolam = new JTextField();
    private final JTextField txtNgayNghi = new JTextField();

    private final JComboBox<Item> cbPhongBan = new JComboBox<>();
    private final JComboBox<Item> cbChucVu = new JComboBox<>();
    private final JComboBox<String> cbTrangThai =
            new JComboBox<>(new String[]{"1 - Đang làm", "0 - Nghỉ việc"});

    private final JTextField txtQuyen = new JTextField();
    private final JButton btnPhanQuyen = new JButton("Phân quyền");

    public PnlQuanLyNhanVien() {

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cbTrangThai.setEnabled(false);
        txtQuyen.setEditable(false);

        btnPhanQuyen.addActionListener(e -> openPermissionDialog());

        try {
            loadPhongBan();
            loadChucVu();
            reload();
        } catch (Exception e) {
            e.printStackTrace();
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });
    }

    private JPanel buildForm() {

        JPanel form = new JPanel(new GridLayout(4,4,10,10));

        form.add(new JLabel("Họ tên"));
        form.add(txtHoTen);

        form.add(new JLabel("Email"));
        form.add(txtEmail);

        form.add(new JLabel("Số điện thoại"));
        form.add(txtDienThoai);

        form.add(new JLabel("Lương cơ bản"));
        form.add(txtLuong);

        form.add(new JLabel("Ngày vào làm (yyyy-MM-dd)"));
        form.add(txtNgayVaolam);

        form.add(new JLabel("Ngày nghỉ (yyyy-MM-dd)"));
        form.add(txtNgayNghi);

        form.add(new JLabel("Phòng ban"));
        form.add(cbPhongBan);

        form.add(new JLabel("Chức vụ"));
        form.add(cbChucVu);

        form.add(new JLabel("Trạng thái"));
        form.add(cbTrangThai);

        form.add(new JLabel("Quyền"));
        form.add(buildPermissionBox());

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> {
            table.clearSelection();
            clearForm();
        });

        JPanel wrap = new JPanel(new BorderLayout(10,10));
        wrap.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnClear);
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);

        wrap.add(actions, BorderLayout.SOUTH);

        return wrap;
    }

    private JPanel buildPermissionBox() {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.add(txtQuyen, BorderLayout.CENTER);
        p.add(btnPhanQuyen, BorderLayout.EAST);
        return p;
    }

    // ================= LOAD DATA =================

    private void loadPhongBan() {
        cbPhongBan.removeAllItems();
        List<PhongBanDTO> list = phongBanBUS.layDanhSach();
        for (PhongBanDTO p : list) {
            cbPhongBan.addItem(new Item(p.getPhongBanId(), p.getTenPhongBan()));
        }
    }

    private void loadChucVu() {
        cbChucVu.removeAllItems();
        List<ChucVuDTO> list = chucVuBUS.layDanhSach();
        for (ChucVuDTO c : list) {
            cbChucVu.addItem(new Item(c.getChucVuId(), c.getTenChucVu()));
        }
    }

    private void reload() {
        model.setRowCount(0);
        for (NhanVienDTO nv : nhanVienBUS.layDanhSach()) {
            model.addRow(new Object[]{
                nv.getNhanVienId(),
                nv.getHoTen(),
                nv.getDienThoai(),
                nv.getEmail(),
                nv.getTenPhongBan(),
                nv.getTenChucVu(),
                nv.getNgayVaoLam(),
                nv.getTrangThai() == 1 ? "Đang làm" : "Nghỉ việc",
                nv.getNgayNghi(),
                nv.getLuongCoBan()
            });
        }
    }

    private void clearForm() {
        txtHoTen.setText("");
        txtEmail.setText("");
        txtDienThoai.setText("");
        txtLuong.setText("");
        txtNgayVaolam.setText("");
        txtNgayNghi.setText("");
        txtQuyen.setText("");

        if (cbPhongBan.getItemCount() > 0) cbPhongBan.setSelectedIndex(0);
        if (cbChucVu.getItemCount() > 0) cbChucVu.setSelectedIndex(0);

        cbTrangThai.setSelectedIndex(0);
        cbTrangThai.setEnabled(false);
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        NhanVienDTO nv = nhanVienBUS.findById(id);

        txtHoTen.setText(nv.getHoTen());
        txtEmail.setText(nv.getEmail());
        txtDienThoai.setText(nv.getDienThoai());
        txtLuong.setText(String.valueOf(nv.getLuongCoBan()));
        txtNgayVaolam.setText(String.valueOf(nv.getNgayVaoLam()));
        txtNgayNghi.setText(
            nv.getNgayNghi() == null ? "" : nv.getNgayNghi().toString()
        );

        selectComboById(cbPhongBan, nv.getPhongBanId());
        selectComboById(cbChucVu, nv.getChucVuId());

        cbTrangThai.setSelectedIndex(nv.getTrangThai() == 1 ? 0 : 1);
        cbTrangThai.setEnabled(true);
        
        loadPermissionSummary(id);
    }


    // ================= CRUD =================

    private void add() {
        try {
            NhanVienDTO nv = getFormData(null);

            String username = txtEmail.getText().trim();
            String password = "123456";

            int nhomQuyenId = 0; // không dùng nhóm quyền ở đây nữa

            nhanVienBUS.themNhanVienVaTaiKhoan(
                    nv,
                    username,
                    password,
                    nhomQuyenId
            );

            reload();
            clearForm();

            JOptionPane.showMessageDialog(
                this,
                "Thêm nhân viên + tạo tài khoản thành công!\nChọn nhân viên trong bảng rồi bấm 'Phân quyền' để cấp quyền."
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa.");
            return;
        }

        try {
            int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
            NhanVienDTO nv = getFormData(id);
            nhanVienBUS.suaNhanVien(nv);
            reload();
            clearForm();
            table.clearSelection();
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xoá nhân viên này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        nhanVienBUS.xoaNhanVien(id);
        reload();
        clearForm();
        table.clearSelection();
    }

    // ================= PHÂN QUYỀN =================

    private void openPermissionDialog() {
    int row = table.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trước.");
        return;
    }

    int nhanVienId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

    List<flightbooking.dto.NhomQuyenDTO> dsNhomQuyen =
            new flightbooking.dao.NhomQuyenDAO().findAll();

    if (dsNhomQuyen == null || dsNhomQuyen.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Chưa có nhóm quyền nào.");
        return;
    }

    Integer currentNhomQuyenId = nhanVienBUS.getNhomQuyenIdByNhanVien(nhanVienId);
    boolean firstTime = (currentNhomQuyenId == null || currentNhomQuyenId <= 0);

    JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Phân quyền cho nhân viên ID " + nhanVienId,
            true
    );
    dialog.setSize(420, 260);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    DefaultComboBoxModel<NhomQuyenItem> cbModel = new DefaultComboBoxModel<>();
    JComboBox<NhomQuyenItem> cbNhomQuyen = new JComboBox<>(cbModel);

    for (flightbooking.dto.NhomQuyenDTO nq : dsNhomQuyen) {
        cbModel.addElement(new NhomQuyenItem(
                nq.getNhomQuyenId(),
                nq.getTenNhomQuyen()
        ));
    }

    if (currentNhomQuyenId != null) {
        for (int i = 0; i < cbModel.getSize(); i++) {
            NhomQuyenItem item = cbModel.getElementAt(i);
            if (item.id == currentNhomQuyenId) {
                cbNhomQuyen.setSelectedIndex(i);
                break;
            }
        }
    }

    JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
    center.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
    center.add(new JLabel("Chọn nhóm quyền"));
    center.add(cbNhomQuyen);

    JButton btnSave = new JButton("Lưu");
    JButton btnUpdate = new JButton("Cập nhật");
    JButton btnClose = new JButton("Đóng");

    btnSave.setEnabled(firstTime);
    btnUpdate.setEnabled(!firstTime);

    btnSave.addActionListener(e -> {
        try {
            NhomQuyenItem selected = (NhomQuyenItem) cbNhomQuyen.getSelectedItem();
            if (selected == null) {
                throw new RuntimeException("Vui lòng chọn nhóm quyền.");
            }

            nhanVienBUS.saveNhomQuyenForNhanVien(nhanVienId, selected.id);
            txtQuyen.setText(selected.text);

            JOptionPane.showMessageDialog(dialog, "Lưu nhóm quyền thành công!");
            dialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, ex.getMessage());
        }
    });

    btnUpdate.addActionListener(e -> {
        try {
            NhomQuyenItem selected = (NhomQuyenItem) cbNhomQuyen.getSelectedItem();
            if (selected == null) {
                throw new RuntimeException("Vui lòng chọn nhóm quyền.");
            }

            nhanVienBUS.updateNhomQuyenForNhanVien(nhanVienId, selected.id);
            txtQuyen.setText(selected.text);

            JOptionPane.showMessageDialog(dialog, "Cập nhật nhóm quyền thành công!");
            dialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, ex.getMessage());
        }
    });

    btnClose.addActionListener(e -> dialog.dispose());

    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    south.add(btnSave);
    south.add(btnUpdate);
    south.add(btnClose);

    JLabel lblHint = new JLabel(
            firstTime
                    ? "Nhân viên này chưa có nhóm quyền. Dùng nút Lưu."
                    : "Nhân viên này đã có nhóm quyền. Dùng nút Cập nhật."
    );
    lblHint.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

    dialog.add(lblHint, BorderLayout.NORTH);
    dialog.add(center, BorderLayout.CENTER);
    dialog.add(south, BorderLayout.SOUTH);

    dialog.setVisible(true);
}


    // ================= HELPER =================

    private NhanVienDTO getFormData(Integer id) {

        NhanVienDTO nv = new NhanVienDTO();

        if (id != null) nv.setNhanVienId(id);

        nv.setHoTen(txtHoTen.getText().trim());
        if (nv.getHoTen().isEmpty())
            throw new RuntimeException("Họ tên không được để trống.");

        nv.setEmail(txtEmail.getText().trim());
        if (nv.getEmail().isEmpty())
            throw new RuntimeException("Email không được để trống.");

        nv.setDienThoai(txtDienThoai.getText().trim());
        if (nv.getDienThoai().isEmpty())
            throw new RuntimeException("Số điện thoại không được để trống.");

        try {
            nv.setLuongCoBan(new BigDecimal(txtLuong.getText().trim()));
        } catch (Exception e) {
            throw new RuntimeException("Lương phải là số hợp lệ.");
        }

        try {
            nv.setNgayVaoLam(LocalDate.parse(txtNgayVaolam.getText().trim()));
        } catch (Exception e) {
            throw new RuntimeException("Ngày vào sai định dạng yyyy-MM-dd.");
        }

        String ngayNghiText = txtNgayNghi.getText().trim();
        if (!ngayNghiText.isEmpty()) {
            try {
                nv.setNgayNghi(LocalDate.parse(ngayNghiText));
            } catch (Exception e) {
                throw new RuntimeException("Ngày nghỉ sai định dạng yyyy-MM-dd.");
            }
        } else {
            nv.setNgayNghi(null);
        }

        Item pb = (Item) cbPhongBan.getSelectedItem();
        Item cv = (Item) cbChucVu.getSelectedItem();

        if (pb != null) nv.setPhongBanId(pb.id);
        if (cv != null) nv.setChucVuId(cv.id);

        if (id == null) {
            nv.setTrangThai(1);
        } else {
            nv.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);
        }

        return nv;
    }

    private void selectComboById(JComboBox<Item> cb, Integer id) {
        if (id == null) return;
        for (int i = 0; i < cb.getItemCount(); i++) {
            Item it = cb.getItemAt(i);
            if (it != null && it.id == id) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    private static class Item {
        final int id;
        final String text;

        Item(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static class NhomQuyenItem {
    final int id;
    final String text;

    NhomQuyenItem(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

private void loadPermissionSummary(int nhanVienId) {
    try {
        Integer nhomQuyenId = nhanVienBUS.getNhomQuyenIdByNhanVien(nhanVienId);

        if (nhomQuyenId == null || nhomQuyenId <= 0) {
            txtQuyen.setText("Chưa phân quyền");
            return;
        }

        List<flightbooking.dto.NhomQuyenDTO> dsNhomQuyen =
                new flightbooking.dao.NhomQuyenDAO().findAll();

        for (flightbooking.dto.NhomQuyenDTO nq : dsNhomQuyen) {
            if (nq.getNhomQuyenId() == nhomQuyenId) {
                txtQuyen.setText(nq.getTenNhomQuyen());
                return;
            }
        }

        txtQuyen.setText("Nhóm quyền #" + nhomQuyenId);

    } catch (Exception e) {
        txtQuyen.setText("Không tải được nhóm quyền");
    }
}

}