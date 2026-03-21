package flightbooking.gui.admin.pnl;

import flightbooking.bus.SanBayBUS;
import flightbooking.dto.SanBayDTO;
import flightbooking.util.ActionConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PnlQuanLySanBay extends JPanel {

    private final SanBayBUS bus = new SanBayBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên sân bay", "Thành phố", "Quốc gia"}, 0
    );
    private final JTable table = new JTable(model);

    private final JTextField txtTen = new JTextField();
    private final JTextField txtThanhPho = new JTextField();
    private final JTextField txtQuocGia = new JTextField();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;

    public PnlQuanLySanBay() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(2,4,10,10));
        form.add(new JLabel("Tên sân bay"));
        form.add(txtTen);
        form.add(new JLabel("Thành phố"));
        form.add(txtThanhPho);
        form.add(new JLabel("Quốc gia"));
        form.add(txtQuocGia);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());

        JPanel wrap = new JPanel(new BorderLayout(10,10));
        wrap.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);

        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }

    private void reload() {
        model.setRowCount(0);
        List<SanBayDTO> list = bus.dsSanBay();
        for (SanBayDTO s : list) {
            model.addRow(new Object[]{
                    s.getSanBayId(),
                    s.getTenSanBay(),
                    s.getThanhPho(),
                    s.getQuocGia()
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
        txtThanhPho.setText(String.valueOf(model.getValueAt(row, 2)));
        txtQuocGia.setText(String.valueOf(model.getValueAt(row, 3)));
    }

    private void add() {
        SanBayDTO s = new SanBayDTO();
        s.setTenSanBay(txtTen.getText().trim());
        s.setThanhPho(txtThanhPho.getText().trim());
        s.setQuocGia(txtQuocGia.getText().trim());

        bus.themSanBay(s);
        reload();
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        SanBayDTO s = new SanBayDTO();
        s.setSanBayId(id);
        s.setTenSanBay(txtTen.getText().trim());
        s.setThanhPho(txtThanhPho.getText().trim());
        s.setQuocGia(txtQuocGia.getText().trim());

        bus.capNhatSanBay(s);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        bus.xoaSanBay(id);
        reload();
    }

    // Gọi hàm này sau khi load quyền
    public void applyPermissions(List<Integer> actionIds) {
    btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
    btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
    btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
    revalidate();
    repaint();
}
}
