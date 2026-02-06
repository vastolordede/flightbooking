package flightbooking.gui.admin.pnl;

import flightbooking.bus.SanBayBUS;
import flightbooking.bus.TuyenBayBUS;
import flightbooking.dto.SanBayDTO;
import flightbooking.dto.TuyenBayDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PnlQuanLyTuyenBay extends JPanel {

    private final TuyenBayBUS tuyenBayBUS = new TuyenBayBUS();
    private final SanBayBUS sanBayBUS = new SanBayBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Sân bay đi", "Sân bay đến", "Số dặm"}, 0
    );
    private final JTable table = new JTable(model);

    private final JComboBox<Item> cbSanBayDi = new JComboBox<>();
    private final JComboBox<Item> cbSanBayDen = new JComboBox<>();
    private final JSpinner spSoDam = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 10));

    public PnlQuanLyTuyenBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadSanBayToCombo();
        reload();

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));

        form.add(new JLabel("Sân bay đi"));
        form.add(cbSanBayDi);

        form.add(new JLabel("Sân bay đến"));
        form.add(cbSanBayDen);

        form.add(new JLabel("Số dặm"));
        form.add(spSoDam);

        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.add(form, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);

        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private void loadSanBayToCombo() {
        cbSanBayDi.removeAllItems();
        cbSanBayDen.removeAllItems();
        List<SanBayDTO> list = sanBayBUS.dsSanBay();
        for (SanBayDTO s : list) {
            cbSanBayDi.addItem(new Item(s.getSanBayId(), s.getTenSanBay()));
            cbSanBayDen.addItem(new Item(s.getSanBayId(), s.getTenSanBay()));
        }
    }

    private void reload() {
        model.setRowCount(0);
        List<TuyenBayDTO> list = tuyenBayBUS.dsTuyenBay();
        for (TuyenBayDTO t : list) {
            String tenDi = findTenSanBay(t.getSanBayDiId());
            String tenDen = findTenSanBay(t.getSanBayDenId());
            model.addRow(new Object[]{t.getTuyenBayId(), tenDi, tenDen, t.getSoDam()});
        }
    }

    private String findTenSanBay(Integer id) {
        if (id == null) return "N/A";
        for (SanBayDTO s : sanBayBUS.dsSanBay()) {
            if (s.getSanBayId() == id) return s.getTenSanBay();
        }
        return "ID=" + id;
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String tenDi = String.valueOf(model.getValueAt(row, 1));
        String tenDen = String.valueOf(model.getValueAt(row, 2));
        int soDam = Integer.parseInt(String.valueOf(model.getValueAt(row, 3)));

        selectComboByText(cbSanBayDi, tenDi);
        selectComboByText(cbSanBayDen, tenDen);
        spSoDam.setValue(soDam);
    }

    private void selectComboByText(JComboBox<Item> cb, String text) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            Item it = cb.getItemAt(i);
            if (it != null && it.text.equals(text)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    private void add() {
        Item di = (Item) cbSanBayDi.getSelectedItem();
        Item den = (Item) cbSanBayDen.getSelectedItem();
        if (di == null || den == null) return;

        if (di.id == den.id) {
            JOptionPane.showMessageDialog(this, "Sân bay đi và đến không được trùng nhau.");
            return;
        }

        TuyenBayDTO t = new TuyenBayDTO();
        t.setSanBayDiId(di.id);
        t.setSanBayDenId(den.id);
        t.setSoDam((int) spSoDam.getValue());

        tuyenBayBUS.themTuyenBay(t);
        reload();
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        Item di = (Item) cbSanBayDi.getSelectedItem();
        Item den = (Item) cbSanBayDen.getSelectedItem();
        if (di == null || den == null) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        TuyenBayDTO t = new TuyenBayDTO();
        t.setTuyenBayId(id);
        t.setSanBayDiId(di.id);
        t.setSanBayDenId(den.id);
        t.setSoDam((int) spSoDam.getValue());

        tuyenBayBUS.capNhatTuyenBay(t);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        tuyenBayBUS.xoaTuyenBay(id);
        reload();
    }

    private static class Item {
        final int id;
        final String text;

        Item(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public String toString() { return text; }
    }
}
