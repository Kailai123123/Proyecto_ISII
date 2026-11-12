package iterator;

import java.util.List;
import domain.Sale;

public class ExtendedIteratorSales implements ExtendedIterator<Sale> {

    private List<Sale> sales;
    private int position;

    public ExtendedIteratorSales(List<Sale> sales) {
        this.sales = sales;
        this.position = 0;
    }

    @Override
    public boolean hasNext() {
        return sales != null && position >= 0 && position < sales.size();
    }

    @Override
    public Sale next() {
        Sale sale = sales.get(position);
        position++;
        return sale;
    }

    @Override
    public boolean hasPrevious() {
        return sales != null && position >= 0 && position < sales.size();
    }

    @Override
    public Sale previous() {
        Sale sale = sales.get(position);
        position--;
        return sale;
    }

    @Override
    public void goFirst() {
        position = 0;
    }

    @Override
    public void goLast() {
        if (sales != null && !sales.isEmpty()) {
            position = sales.size() - 1;
        } else {
            position = 0;
        }
    }
}