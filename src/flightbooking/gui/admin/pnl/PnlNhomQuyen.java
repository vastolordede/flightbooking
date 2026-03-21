package flightbooking.gui.admin.pnl;

import flightbooking.bus.NhomQuyenBUS;
import flightbooking.dao.NhomQuyenDAO;
import flightbooking.dao.QuyenDAO;
import flightbooking.dao.QuyenActionDAO;
import flightbooking.dto.NhomQuyenDTO;
import flightbooking.dto.QuyenDTO;
import flightbooking.util.ActionConstants;
import flightbooking.dto.QuyenActionDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PnlNhomQuyen extends JPanel {

    private final JTextField txtTen = new JTextField();
    private final JPanel pnlCheck = new JPanel();

    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private final Map<Integer, JPanel> mapPanelCon = new LinkedHashMap<>();
    private final Map<String, Integer> actionMap = new HashMap<>();

    private final NhomQuyenBUS bus = new NhomQuyenBUS();

    private JTable tbl;
    private DefaultTableModel model;

    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    // map quyền -> action hiển thị
    private static final Map<String, String[]> ACTION_MAP = new HashMap<>();
    static {
        ACTION_MAP.put("quản lý sân bay", new String[]{"Thêm", "Sửa", "Xóa"});
        ACTION_MAP.put("quản lý tuyến bay", new String[]{"Thêm", "Sửa", "Xóa"});
        ACTION_MAP.put("quản lý chuyến bay", new String[]{"Thêm", "Sửa", "Xóa", "Giá hạng ghế", "Sơ đồ ghế"});
        ACTION_MAP.put("đặt vé (quầy)", new String[]{"Tạo vé"});
        ACTION_MAP.put("quản lý hãng hàng không", new String[]{"Thêm", "Sửa", "Xóa"});
        ACTION_MAP.put("quản lý máy bay", new String[]{"Thêm", "Sửa", "Xóa", "Tạo ghế"});
        ACTION_MAP.put("quản lý nhân viên", new String[]{"Thêm", "Sửa", "Xóa", "Phân quyền"});
        ACTION_MAP.put("quản lý nhóm quyền", new String[]{"Tạo", "Cập nhật", "Xóa"});
    }

    private static final String[] DEFAULT_ACTIONS = {"Thêm", "Sửa", "Xóa"};

    public PnlNhomQuyen() {

        setLayout(new BorderLayout(10, 10));
        add(buildTop(), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Tên"}, 0);
        tbl = new JTable(model);
        tbl.setPreferredScrollableViewportSize(new Dimension(250, 0));
        add(new JScrollPane(tbl), BorderLayout.WEST);

        pnlCheck.setLayout(new BoxLayout(pnlCheck, BoxLayout.Y_AXIS));
        add(new JScrollPane(pnlCheck), BorderLayout.CENTER);

        loadActionMap(); // 🔥 load action từ DB
        loadQuyen();
        loadTable();
        bindTable();
    }

    private JPanel buildTop() {

        JPanel p = new JPanel(new GridLayout(2, 3, 10, 10));
        p.add(new JLabel("Tên nhóm quyền"));
        p.add(txtTen);

         btnSave = new JButton("Tạo");
        btnSave.addActionListener(e -> save());

         btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> update());

         btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> delete());

        p.add(btnSave);
        p.add(btnUpdate);
        p.add(btnDelete);

        return p;
    }

    private void loadActionMap() {
        List<QuyenActionDTO> list = new QuyenActionDAO().findAll();
        for (QuyenActionDTO a : list) {
            actionMap.put(a.getTenquyen().toLowerCase(), a.getId());
        }
    }

    private void loadQuyen() {

        List<QuyenDTO> list = new QuyenDAO().findAll();

        for (QuyenDTO q : list) {

            int qid = q.getQuyenId();
            String tenQuyen = q.getTenQuyen();

            JCheckBox cbCha = new JCheckBox(tenQuyen);
            cbCha.setFont(cbCha.getFont().deriveFont(Font.BOLD));
            cbCha.putClientProperty("id", qid);
            checkBoxes.add(cbCha);

            String[] actions = ACTION_MAP.getOrDefault(
                    tenQuyen.toLowerCase().trim(),
                    DEFAULT_ACTIONS
            );

            JPanel panelCon = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelCon.setBorder(BorderFactory.createEmptyBorder(0, 24, 4, 0));
            panelCon.setVisible(false);

            for (String action : actions) {

                JCheckBox cbCon = new JCheckBox(action);

                Integer actionId = actionMap.get(action.toLowerCase());
                if (actionId != null) {
                    cbCon.putClientProperty("id", actionId); // 🔥 gắn ID
                }

                panelCon.add(cbCon);
            }

            mapPanelCon.put(qid, panelCon);

            cbCha.addActionListener(e -> {
                boolean checked = cbCha.isSelected();
                panelCon.setVisible(checked);

                if (!checked) {
                    for (Component c : panelCon.getComponents()) {
                        if (c instanceof JCheckBox)
                            ((JCheckBox) c).setSelected(false);
                    }
                }

                pnlCheck.revalidate();
                pnlCheck.repaint();
            });

            JPanel row = new JPanel(new BorderLayout());
            row.add(cbCha, BorderLayout.NORTH);
            row.add(panelCon, BorderLayout.CENTER);

            pnlCheck.add(row);
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

        // 🔥 LẤY ACTION ĐÚNG CHỖ
        List<Integer> actions = new NhomQuyenDAO().getActionByNhom(id);

        for (JCheckBox cb : checkBoxes) {

            int qid = (int) cb.getClientProperty("id");
            boolean has = perms.contains(qid);
            cb.setSelected(has);

            JPanel panelCon = mapPanelCon.get(qid);

            if (panelCon != null) {

                panelCon.setVisible(has);

                for (Component c : panelCon.getComponents()) {

                    if (c instanceof JCheckBox) {
                        JCheckBox cbCon = (JCheckBox) c;

                        Integer aid = (Integer) cbCon.getClientProperty("id");

                        if (aid != null) {
                            cbCon.setSelected(actions.contains(aid));
                        }
                    }
                }
            }
        }

        pnlCheck.revalidate();
        pnlCheck.repaint();
    });
}

    private List<Integer> getSelected() {
        List<Integer> list = new ArrayList<>();
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) list.add((Integer) cb.getClientProperty("id"));
        }
        return list;
    }

    private List<Integer> getSelectedAction() {

        List<Integer> list = new ArrayList<>();

        for (JPanel panel : mapPanelCon.values()) {
            for (Component c : panel.getComponents()) {

                if (c instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) c;

                    if (cb.isSelected()) {
                        list.add((Integer) cb.getClientProperty("id"));
                    }
                }

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

            // 🔥 lưu action
            bus.saveActionForNhom(id, getSelectedAction());

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

        for (JCheckBox cb : checkBoxes) cb.setSelected(false);

        for (JPanel p : mapPanelCon.values()) p.setVisible(false);

        pnlCheck.revalidate();
        pnlCheck.repaint();
    }

    public void applyPermissions(List<Integer> actionIds) {
    btnSave.setVisible(actionIds.contains(ActionConstants.THEM));
    btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
    btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
    revalidate();
    repaint();
}
}