package flightbooking.gui.admin.pnl;

import flightbooking.bus.NhomQuyenBUS;
import flightbooking.dao.NhomQuyenDAO;
import flightbooking.dao.QuyenDAO;
import flightbooking.dto.NhomQuyenDTO;
import flightbooking.dto.QuyenDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PnlNhomQuyen extends JPanel {

    private final JTextField txtTen = new JTextField();
    private final JPanel pnlCheck = new JPanel(new GridLayout(0, 2));

    private final List<JCheckBox> checkBoxes = new ArrayList<>();

    private final NhomQuyenBUS bus = new NhomQuyenBUS();

    private JTable tbl;
    private DefaultTableModel model;

    public PnlNhomQuyen() {

        setLayout(new BorderLayout(10,10));

        add(buildTop(), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Tên"}, 0);
        tbl = new JTable(model);
        tbl.setPreferredScrollableViewportSize(new Dimension(250, 0));

        add(new JScrollPane(tbl), BorderLayout.WEST);
        add(new JScrollPane(pnlCheck), BorderLayout.CENTER);

        loadQuyen();
        loadTable();
        bindTable();
    }

    private JPanel buildTop() {

        JPanel p = new JPanel(new GridLayout(2,3,10,10));

        p.add(new JLabel("Tên nhóm quyền"));
        p.add(txtTen);

        JButton btnSave = new JButton("Tạo");
        btnSave.addActionListener(e -> save());

        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> update());

        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> delete());

        p.add(btnSave);
        p.add(btnUpdate);
        p.add(btnDelete);

        return p;
    }

    private void loadQuyen() {

        List<QuyenDTO> list = new QuyenDAO().findAll();

        for (QuyenDTO q : list) {
            JCheckBox cb = new JCheckBox(q.getTenQuyen());
            cb.putClientProperty("id", q.getQuyenId());

            checkBoxes.add(cb);
            pnlCheck.add(cb);
        }
    }

    private void loadTable() {

        model.setRowCount(0);

        List<NhomQuyenDTO> list = new NhomQuyenDAO().findAll();

        for (NhomQuyenDTO n : list) {
            model.addRow(new Object[]{
                    n.getNhomQuyenId(),
                    n.getTenNhomQuyen()
            });
        }
    }

    private void bindTable() {

        tbl.getSelectionModel().addListSelectionListener(e -> {

            int row = tbl.getSelectedRow();
            if (row < 0) return;

            int id = (int) model.getValueAt(row, 0);

            txtTen.setText(model.getValueAt(row, 1).toString());

            List<Integer> perms = new NhomQuyenDAO().getPermissionsByNhom(id);

            for (JCheckBox cb : checkBoxes) {
                int qid = (int) cb.getClientProperty("id");
                cb.setSelected(perms.contains(qid));
            }
        });
    }

    private List<Integer> getSelected() {

        List<Integer> list = new ArrayList<>();

        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                list.add((Integer) cb.getClientProperty("id"));
            }
        }

        return list;
    }

    private void save() {

        try {
            bus.createNhomQuyen(txtTen.getText(), getSelected());

            loadTable();
            clearForm();

            JOptionPane.showMessageDialog(this, "Tạo thành công!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void update() {

        int row = tbl.getSelectedRow();
        if (row < 0) return;

        int id = (int) model.getValueAt(row, 0);

        try {
            bus.updateNhomQuyen(id, txtTen.getText(), getSelected());

            loadTable();

            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void delete() {

        int row = tbl.getSelectedRow();
        if (row < 0) return;

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this, "Xóa nhóm quyền này?", "Xác nhận", JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            bus.deleteNhomQuyen(id);

            loadTable();
            clearForm();

            JOptionPane.showMessageDialog(this, "Xóa thành công!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void clearForm() {

        txtTen.setText("");

        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(false);
        }
    }
}