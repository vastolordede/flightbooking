package flightbooking.gui.admin.pnl;

import flightbooking.bus.ChuyenBayBUS;
import flightbooking.bus.DatVeBUS;
import flightbooking.dto.ChuyenBayDTO;
import flightbooking.dto.GheDTO;
import flightbooking.dto.HanhKhachDTO;
import flightbooking.util.SessionContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PnlDatVeAdmin extends JPanel {

    private final DatVeBUS datVeBUS = new DatVeBUS();
    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();

    // combobox
    private final JComboBox<ChuyenItem> cbChuyen = new JComboBox<>();
    private final JComboBox<GheItem> cbGhe = new JComboBox<>();

    // bảng vé
    private JTable tableVe;
    private DefaultTableModel modelVe;

    // input
    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtSoGiayTo = new JTextField();

    private final JComboBox<String> cbPay =
            new JComboBox<>(new String[]{"cash", "card"});

    public PnlDatVeAdmin() {

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        loadChuyenBay();
    }

    // ================= FORM =================

    private JPanel buildForm() {

        JPanel form = new JPanel(new GridLayout(3,4,10,10));

        form.add(new JLabel("Chuyến bay"));
        form.add(cbChuyen);

        form.add(new JLabel("Ghế (còn trống)"));
        form.add(cbGhe);

        form.add(new JLabel("Họ tên hành khách"));
        form.add(txtHoTen);

        form.add(new JLabel("Số giấy tờ"));
        form.add(txtSoGiayTo);

        form.add(new JLabel("Thanh toán"));
        form.add(cbPay);

        cbChuyen.addActionListener(e -> loadGheTheoChuyen());

        JButton btnTaoVe = new JButton("Tạo vé");
        btnTaoVe.addActionListener(e -> taoVe());

        JPanel wrap = new JPanel(new BorderLayout(10,10));
        wrap.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnTaoVe);

        wrap.add(actions, BorderLayout.SOUTH);

        return wrap;
    }

    // ================= TABLE =================

    private JComponent buildTable() {

        modelVe = new DefaultTableModel(
                new Object[]{
                        "Mã vé",
                        "Chuyến bay",
                        "Ghế",
                        "Hành khách",
                        "Giá (VNĐ)"
                },0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableVe = new JTable(modelVe);

        JScrollPane sp = new JScrollPane(tableVe);
        sp.setPreferredSize(new Dimension(800,400));

        return sp;
    }

    // ================= LOAD CHUYẾN BAY =================

    private void loadChuyenBay() {

        cbChuyen.removeAllItems();

        List<ChuyenBayDTO> list = chuyenBayBUS.dsChuyenBay();

        for (ChuyenBayDTO c : list) {

            String tuyen =
                    (c.getSanBayDiTen()!=null && c.getSanBayDenTen()!=null)
                            ? c.getSanBayDiTen()+" → "+c.getSanBayDenTen()
                            : "Tuyến #" + c.getTuyenBayId();

            String text =
                    "CB#" + c.getChuyenBayId()
                            + " • " + tuyen
                            + " • " + c.getGioKhoiHanh();

            cbChuyen.addItem(
                    new ChuyenItem(c.getChuyenBayId(), text)
            );
        }

        loadGheTheoChuyen();
    }

    // ================= LOAD GHẾ =================

    private void loadGheTheoChuyen() {

        cbGhe.removeAllItems();

        ChuyenItem it = (ChuyenItem) cbChuyen.getSelectedItem();

        if(it==null) return;

        List<GheDTO> all =
                datVeBUS.dsGheCuaChuyen(it.id);

        List<GheDTO> free =
                all.stream()
                        .filter(g ->
                                !(g.isDaDat()
                                        || (g.getTrangThai()!=null
                                        && g.getTrangThai()==0)))
                        .sorted((a,b)->
                                String.valueOf(a.getTenGhe())
                                        .compareToIgnoreCase(
                                                String.valueOf(b.getTenGhe())
                                        ))
                        .collect(Collectors.toList());

        for(GheDTO g:free){

            int tang =
                    (g.getTang()==null?1:g.getTang());

            String text =
                    "Tầng "+tang+" - "+g.getTenGhe();

            cbGhe.addItem(
                    new GheItem(g.getGheId(), text)
            );
        }
    }

    // ================= TẠO VÉ =================

    private void taoVe() {

        ChuyenItem cb =
                (ChuyenItem) cbChuyen.getSelectedItem();

        GheItem ghe =
                (GheItem) cbGhe.getSelectedItem();

        String ten =
                txtHoTen.getText().trim();

        String giayto =
                txtSoGiayTo.getText().trim();

        if(cb==null || ghe==null || ten.isEmpty()){

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn chuyến, chọn ghế, nhập họ tên."
            );

            return;
        }

        try{

            int chuyenBayId = cb.id;
            int gheId = ghe.id;

            DatVeBUS.ThongTinHanhKhachVaGhe item =
                    new DatVeBUS.ThongTinHanhKhachVaGhe();

            HanhKhachDTO hk = new HanhKhachDTO();

            hk.setHoTen(ten);
            hk.setSoGiayTo(giayto);

            item.setHanhKhach(hk);
            item.setGheId(gheId);

            List<DatVeBUS.ThongTinHanhKhachVaGhe> items =
                    new ArrayList<>();

            items.add(item);

            Integer taiKhoanNhanVienId =
                    SessionContext.getNhanVienId();

            if(taiKhoanNhanVienId==null)
                taiKhoanNhanVienId = 1;

            DatVeBUS.KetQuaDatVe kq =
                    datVeBUS.datVe(
                            null,
                            taiKhoanNhanVienId,
                            chuyenBayId,
                            items,
                            (String) cbPay.getSelectedItem()
                    );

            // thêm vào bảng
            modelVe.addRow(new Object[]{
                    kq.getVeIds().get(0),
                    "CB#" + chuyenBayId,
                    ghe.text,
                    ten,
                    kq.getTongTien()
            });

            JOptionPane.showMessageDialog(
                    this,
                    "Tạo vé thành công!"
            );

            loadGheTheoChuyen();

        }
        catch(Exception ex){

            JOptionPane.showMessageDialog(
                    this,
                    "Không tạo được vé: " + ex.getMessage()
            );
        }
    }

    // ================= CLASS ITEM =================

    private static class ChuyenItem{

        final int id;
        final String text;

        ChuyenItem(int id,String text){
            this.id=id;
            this.text=text;
        }

        @Override
        public String toString(){
            return text;
        }
    }

    private static class GheItem{

        final int id;
        final String text;

        GheItem(int id,String text){
            this.id=id;
            this.text=text;
        }

        @Override
        public String toString(){
            return text;
        }
    }
}