package flightbooking.gui.admin.pnl;

import flightbooking.bus.GheGeneratorBUS;
import flightbooking.bus.MayBayBUS;
import flightbooking.dto.MayBayDTO;
import flightbooking.dao.GheDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PnlQuanLyMayBay extends JPanel {

    private final MayBayBUS mayBayBUS = new MayBayBUS();
    private final GheGeneratorBUS gheGenBUS = new GheGeneratorBUS();
    private final GheDAO gheDAO = new GheDAO();

    // Đã bỏ cột "Số tầng", chỉ còn 4 cột
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên máy bay", "Kiểu", "Tổng ghế"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private final JTextField txtTen = new JTextField();
    private final JTextField txtKieu = new JTextField();

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

        // Đã bỏ các ô nhập Tầng và Tổng ghế, chỉnh lại Layout cho gọn
        JPanel form = new JPanel(new GridLayout(1, 4, 10, 10));
        form.add(new JLabel("Tên máy bay:"));
        form.add(txtTen);
        form.add(new JLabel("Kiểu máy bay:"));
        form.add(txtKieu);

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

        JButton btnGen = new JButton("Tạo ghế cho máy bay đang chọn");
        btnGen.addActionListener(e -> openGenSeatDialog());

        JPanel genActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        genActions.add(btnGen);

        JPanel genWrap = new JPanel(new BorderLayout(10,10));
        genWrap.add(genActions, BorderLayout.SOUTH);

        wrap.add(form, BorderLayout.NORTH);
        wrap.add(actions, BorderLayout.CENTER);
        wrap.add(genWrap, BorderLayout.SOUTH);
        return wrap;
    }

    private void reload() {
        model.setRowCount(0);
        for (MayBayDTO m : mayBayBUS.dsMayBay()) {
            // Hiển thị chữ "null" nếu tổng số ghế chưa có hoặc = 0
            Object tongGhe = (m.getTongSoGhe() == null || m.getTongSoGhe() == 0) ? "null" : m.getTongSoGhe();
            model.addRow(new Object[]{
                    m.getMayBayId(),
                    m.getTenMayBay(),
                    m.getKieuMayBay(),
                    tongGhe
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
        txtKieu.setText(String.valueOf(model.getValueAt(row, 2)));
    }

    private void add() {
        MayBayDTO m = new MayBayDTO();
        m.setTenMayBay(txtTen.getText().trim());
        m.setKieuMayBay(txtKieu.getText().trim());
        
        // Mặc định set null cho ghế khi mới thêm máy bay
        m.setTongSoGhe(0);
        // m.setSoTang(1); // Mở comment nếu Database của bạn vẫn bắt buộc có thuộc tính so_tang

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

        // Lấy lại giá trị tổng số ghế từ bảng để không bị đè mất khi Update tên/kiểu
        Object tsg = model.getValueAt(row, 3);
        if (tsg != null && !tsg.toString().equals("null")) {
            m.setTongSoGhe(Integer.parseInt(tsg.toString()));
        } else {
            m.setTongSoGhe(null);
        }

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

    private void openGenSeatDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 máy bay trước.");
            return;
        }

        int mayBayId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Cấu hình ghế - Máy bay ID=" + mayBayId,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Sắp xếp lại thứ tự xuất hiện trong danh sách chọn (không đổi ID, chỉ đổi thứ tự)
        HangGheItem[] danhSachHangGhe = new HangGheItem[]{
            new HangGheItem(3, "First Class"),     // Đưa lên đầu
            new HangGheItem(2, "Business"), 
            new HangGheItem(4, "Premium Economy"),
            new HangGheItem(1, "Economy")          // Đưa xuống cuối
        };

        JPanel pnlConfigList = new JPanel();
        pnlConfigList.setLayout(new BoxLayout(pnlConfigList, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(pnlConfigList);
        
        JButton btnAddConfig = new JButton("+ Thêm hạng ghế");
        List<ConfigRow> configRows = new ArrayList<>();

        Runnable addRowUI = () -> {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            JComboBox<HangGheItem> cbHangGhe = new JComboBox<>(danhSachHangGhe);
            JSpinner spSoGhe = new JSpinner(new SpinnerNumberModel(50, 1, 500, 1));
            JSpinner spSoHang = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
            JButton btnRemove = new JButton("Xóa");
            
            rowPanel.add(new JLabel("Hạng:"));
            rowPanel.add(cbHangGhe);
            rowPanel.add(new JLabel("Tổng số ghế:"));
            rowPanel.add(spSoGhe);
            rowPanel.add(new JLabel("Số hàng:"));
            rowPanel.add(spSoHang);
            rowPanel.add(btnRemove);

            pnlConfigList.add(rowPanel);
            pnlConfigList.revalidate();
            pnlConfigList.repaint();

            ConfigRow configData = new ConfigRow(cbHangGhe, spSoGhe, spSoHang, rowPanel);
            configRows.add(configData);

            btnRemove.addActionListener(e -> {
                pnlConfigList.remove(rowPanel);
                configRows.remove(configData);
                pnlConfigList.revalidate();
                pnlConfigList.repaint();
            });
        };

        addRowUI.run();
        btnAddConfig.addActionListener(e -> addRowUI.run());

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(btnAddConfig);
        pnlTop.add(new JLabel("  (Cột sẽ được tự động tính = Tổng ghế / Số hàng)"));

        JButton btnOk = new JButton("Bắt đầu tạo");
        JButton btnCancel = new JButton("Hủy");

        btnOk.addActionListener(e -> {
            try {
                if (!gheDAO.findByMayBay(mayBayId).isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Máy bay đã có ghế trong Database.\nHãy xóa cũ trước khi tạo lại.");
                    return;
                }

                if (configRows.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng thêm ít nhất 1 cấu hình hạng ghế.");
                    return;
                }

                int tongGheDaTao = 0;
                int rowOffset = 0;

                for (ConfigRow rowData : configRows) {
                    HangGheItem selectedHangGhe = (HangGheItem) rowData.cbHangGhe.getSelectedItem();
                    int hangGheId = selectedHangGhe.id;
                    String tenHang = selectedHangGhe.ten;
                    
                    int soGhe = (int) rowData.spSoGhe.getValue();
                    int soHang = (int) rowData.spSoHang.getValue();
                    int soCot = (int) Math.ceil((double) soGhe / soHang);

                    gheGenBUS.taoGheTheoHang(
        mayBayId,
        hangGheId,
        tenHang,
        soGhe,
        soHang,
        rowOffset
);
                    
                    rowOffset += soHang; 
                    tongGheDaTao += (soHang * soCot);
                }

                // Cập nhật lại số lượng ghế vào database
                MayBayDTO mbUpdate = new MayBayDTO();
                mbUpdate.setMayBayId(mayBayId);
                // Giữ lại tên và kiểu (cần truy vấn lại hoặc dùng tạm dữ liệu từ bảng)
                mbUpdate.setTenMayBay(String.valueOf(model.getValueAt(row, 1)));
                mbUpdate.setKieuMayBay(String.valueOf(model.getValueAt(row, 2)));
                mbUpdate.setTongSoGhe(tongGheDaTao);
                mayBayBUS.capNhatMayBay(mbUpdate);

                JOptionPane.showMessageDialog(dialog, "Thành công! Đã tự động tạo " + tongGheDaTao + " ghế.");
                dialog.dispose();
                reload(); // Tải lại bảng để hiển thị số lượng ghế mới

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCancel); actions.add(btnOk);

        dialog.add(pnlTop, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static class ConfigRow {
        JComboBox<HangGheItem> cbHangGhe;
        JSpinner spSoGhe, spSoHang;
        JPanel panel;

        public ConfigRow(JComboBox<HangGheItem> cbHangGhe, JSpinner spSoGhe, JSpinner spSoHang, JPanel panel) {
            this.cbHangGhe = cbHangGhe;
            this.spSoGhe = spSoGhe;
            this.spSoHang = spSoHang;
            this.panel = panel;
        }
    }

    private static class HangGheItem {
        int id; String ten;
        public HangGheItem(int id, String ten) {
            this.id = id; this.ten = ten;
        }
        @Override
        public String toString() { return ten; }
    }
}