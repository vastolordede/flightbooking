package flightbooking.gui.admin.pnl;

import flightbooking.bus.GheGeneratorBUS;
import flightbooking.bus.MayBayBUS;
import flightbooking.dto.MayBayDTO;
import flightbooking.dao.GheDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PnlQuanLyMayBay extends JPanel {

    private final MayBayBUS mayBayBUS = new MayBayBUS();
    private final GheGeneratorBUS gheGenBUS = new GheGeneratorBUS();
    // thêm field
    private final GheDAO gheDAO = new GheDAO();


    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên máy bay", "Kiểu", "Tổng ghế", "Số tầng"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private final JTextField txtTen = new JTextField();
    private final JTextField txtKieu = new JTextField();
    private final JSpinner spTongGhe = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
    private final JSpinner spSoTang = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

    // tạo ghế
    private final JTextField txtTenHang = new JTextField("Economy");
    private final JTextField txtHangGheId = new JTextField("1"); // mock
    private final JSpinner spSoHang = new JSpinner(new SpinnerNumberModel(6, 1, 30, 1));
    private final JSpinner spSoCot = new JSpinner(new SpinnerNumberModel(6, 1, 20, 1));

    public PnlQuanLyMayBay() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JComponent buildTop() {
        JPanel wrap = new JPanel(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(2, 5, 10, 10));
        form.add(new JLabel("Tên máy bay"));
        form.add(txtTen);
        form.add(new JLabel("Kiểu máy bay"));
        form.add(txtKieu);
        form.add(new JLabel("Tổng số ghế (0 = bỏ trống)"));
        form.add(spTongGhe);
        form.add(new JLabel("Số tầng"));
        form.add(spSoTang);

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

        JPanel gen = new JPanel(new GridLayout(2, 6, 10, 10));
        gen.setBorder(BorderFactory.createTitledBorder("Tạo ghế theo hạng + tầng (E.A1)"));
        gen.add(new JLabel("Tên hạng (Economy/...)"));
        gen.add(txtTenHang);
        gen.add(new JLabel("HangGheId (mock)"));
        gen.add(txtHangGheId);
        gen.add(new JLabel("Số hàng (A,B,...)"));
        gen.add(spSoHang);
        gen.add(new JLabel("Số cột (1..n)"));
        gen.add(spSoCot);

        JButton btnGen = new JButton("Tạo ghế cho máy bay đang chọn");
        btnGen.addActionListener(e -> genSeat());

        JPanel genActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        genActions.add(btnGen);

        JPanel genWrap = new JPanel(new BorderLayout(10,10));
        genWrap.add(gen, BorderLayout.CENTER);
        genWrap.add(genActions, BorderLayout.SOUTH);

        wrap.add(form, BorderLayout.NORTH);
        wrap.add(actions, BorderLayout.CENTER);
        wrap.add(genWrap, BorderLayout.SOUTH);
        return wrap;
    }

    private void reload() {
        model.setRowCount(0);
        for (MayBayDTO m : mayBayBUS.dsMayBay()) {
            model.addRow(new Object[]{
                    m.getMayBayId(),
                    m.getTenMayBay(),
                    m.getKieuMayBay(),
                    m.getTongSoGhe(),
                    m.getSoTang()
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
        txtKieu.setText(String.valueOf(model.getValueAt(row, 2)));

        Object tsg = model.getValueAt(row, 3);
        spTongGhe.setValue(tsg == null ? 0 : Integer.parseInt(String.valueOf(tsg)));

        spSoTang.setValue(Integer.parseInt(String.valueOf(model.getValueAt(row, 4))));
    }

    private void add() {
        MayBayDTO m = new MayBayDTO();
        m.setTenMayBay(txtTen.getText().trim());
        m.setKieuMayBay(txtKieu.getText().trim());

        int tsg = (int) spTongGhe.getValue();
        m.setTongSoGhe(tsg <= 0 ? null : tsg);

        m.setSoTang((int) spSoTang.getValue());

        mayBayBUS.themMayBay(m);
        reload();
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        MayBayDTO m = new MayBayDTO();
        m.setMayBayId(id);
        m.setTenMayBay(txtTen.getText().trim());
        m.setKieuMayBay(txtKieu.getText().trim());

        int tsg = (int) spTongGhe.getValue();
        m.setTongSoGhe(tsg <= 0 ? null : tsg);

        m.setSoTang((int) spSoTang.getValue());

        mayBayBUS.capNhatMayBay(m);
        reload();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        mayBayBUS.xoaMayBay(id);
        reload();
    }

    private void genSeat() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 máy bay trước.");
            return;
        }

        int mayBayId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        int soTang = Integer.parseInt(String.valueOf(model.getValueAt(row, 4)));

        int hangGheId = Integer.parseInt(txtHangGheId.getText().trim());
        String tenHang = txtTenHang.getText().trim();
        int soHang = (int) spSoHang.getValue();
        int soCot = (int) spSoCot.getValue();
        // ✅ chặn generate nếu đã có ghế
if (!gheDAO.findByMayBay(mayBayId).isEmpty()) {
    JOptionPane.showMessageDialog(this,
            "Máy bay ID=" + mayBayId + " đã có ghế trong DB.\n" +
            "Không cho generate thêm để tránh trùng.\n" +
            "Nếu muốn generate lại, hãy xóa ghế cũ trước.");
    return;
}

        gheGenBUS.taoGheTheoHang(mayBayId, hangGheId, tenHang, soTang, soHang, soCot);

        JOptionPane.showMessageDialog(this, "Đã tạo ghế E.A1... cho máy bay ID=" + mayBayId);
    }
}
