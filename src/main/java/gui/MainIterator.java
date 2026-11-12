package gui;

import businessLogic.BLFacade;
import businessLogic.BLFactory;
import configuration.ConfigXML;
import domain.Sale;
import iterator.ExtendedIterator;

public class MainIterator {

    public static void main(String[] args) {
        try {
            ConfigXML c = ConfigXML.getInstance();
            BLFacade bl = new BLFactory().getBusinessLogicFactory(c.isBusinessLogicLocal());

            ExtendedIterator<Sale> it = bl.getSalesIterator("");

            System.out.println("----- FROM LAST TO FIRST -----");
            it.goLast();
            while (it.hasPrevious()) {
                Sale s = it.previous();
                System.out.println(s);
            }

            System.out.println("\n----- FROM FIRST TO LAST -----");
            it.goFirst();
            while (it.hasNext()) {
                Sale s = it.next();
                System.out.println(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}