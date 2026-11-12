package gui;

import java.awt.BorderLayout;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import adapter.SellerAdapter;
import businessLogic.BLFacade;
import businessLogic.BLFactory;
import configuration.ConfigXML;
import domain.SaleDetails;
import domain.Seller;

public class MainAdapter {

    public static void main(String[] args) {
        try {
            ConfigXML c = ConfigXML.getInstance();
            BLFacade bl = new BLFactory().getBusinessLogicFactory(c.isBusinessLogicLocal());

            Seller seller = (Seller) bl.getUsuario("aitor@gmail.com");

            if (seller == null) {
                System.out.println("Usuario 'aitor@gmail.com' no encontrado en BD. Generando vendedor de prueba...");
                seller = new Seller("aitor@gmail.com", "Aitor Fernandez", "1234");
                seller.addSale(new SaleDetails("futbol baloia", "Baloia egoera onean", 1, 2.0f, new Date(), null));
                seller.addSale(new SaleDetails("salomon mendiko botak", "Botak 42 neurria", 1, 2.0f, new Date(), null));
                seller.addSale(new SaleDetails("samsung 42\" telebista", "Telebista Full HD", 1, 1.0f, new Date(), null));
            }

            JFrame frame = new JFrame(seller.getName() + "'s sales");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 300);

            SellerAdapter adapter = new SellerAdapter(seller);
            JTable table = new JTable(adapter);

            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}