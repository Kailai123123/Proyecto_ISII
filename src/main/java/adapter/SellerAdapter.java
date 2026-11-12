package adapter;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import domain.Sale;
import domain.Seller;

public class SellerAdapter extends AbstractTableModel {

    private Seller seller;
    private List<Sale> sales;
    private String[] columnNames = {"title", "description", "price"};

    public SellerAdapter(Seller seller) {
        this.seller = seller;
        if (seller != null && seller.getSales() != null) {
            this.sales = new ArrayList<>(seller.getSales());
        } else {
            this.sales = new ArrayList<>();
        }
    }

    @Override
    public int getRowCount() {
        return sales.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Sale sale = sales.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return sale.getTitle();
            case 1:
                return sale.getDescription();
            case 2:
                return sale.getPrice();
            default:
                return null;
        }
    }
}