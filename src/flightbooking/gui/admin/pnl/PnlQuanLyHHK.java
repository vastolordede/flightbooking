package flightbooking.gui.admin.pnl;

import flightbooking.bus.HangHangKhongBUS;
import flightbooking.dto.HangHangKhongDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PnlQuanLyHHK extends JPanel {
    private final HangHangKhongBUS hhkBUS = new HangHangKhongBUS();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên hãng"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);
    private final JTextField txtTen = new JTextField();

    public PnlQuanLyHHK() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JComponent buildTop() {
        JPanel form = new JPanel(new GridLayout(1, 2, 10, 10));
        form.add(new JLabel("Tên hãng hàng không:"));
        form.add(txtTen);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnAdd); actions.add(btnUpdate); actions.add(btnDelete);

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.add(form, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private void reload() {
        model.setRowCount(0);
        try {
            for (HangHangKhongDTO h : hhkBUS.getDsHHK()) {
                model.addRow(new Object[]{ h.getHangHangKhongId(), h.getTenHang() });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
    }

    private void add() {
        HangHangKhongDTO h = new HangHangKhongDTO();
        h.setTenHang(txtTen.getText().trim());
        hhkBUS.themHHK(h);
        reload();
        txtTen.setText("");
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        HangHangKhongDTO h = new HangHangKhongDTO();
        h.setHangHangKhongId((int) model.getValueAt(row, 0));
        h.setTenHang(txtTen.getText().trim());
        hhkBUS.capNhatHHK(h);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        hhkBUS.xoaHHK(id);
        reload();
        txtTen.setText("");
    }
}