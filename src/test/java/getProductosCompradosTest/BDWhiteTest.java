package getProductosCompradosTest;

import domain.Buyer;
import domain.Offer;
import domain.Sale;
import domain.Seller;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import dataAccess.DataAccess;
import testOperations.*;

import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

public class BDWhiteTest {

    static DataAccess sut;
    static TestDataAccess2 testDA;

    private String buyerEmail = "compradorBD_test@mail.com";
    private String sellerEmail = "vendedorBD_test@mail.com";
    private String title = "BiciBD";
    private String desc = "Bicicleta BD test";
    
    private Buyer buyer;
    private Seller seller;
    private Sale sale;
    private Offer offer;

    @Before
    public void setUp() {
        testDA = new TestDataAccess2();
        testDA.open(); 
        
        sut = new DataAccess(); 
        
        try {
            testDA.removeUsuario(buyerEmail); 
            testDA.removeUsuario(sellerEmail);
            testDA.close();
            
            sut.open();
            sut.crearUsuario(buyerEmail, "123", "Buyer", "Test Buyer");
            sut.crearUsuario(sellerEmail, "123", "Seller", "Test Seller");
            
            buyer = (Buyer) sut.getUsuario(buyerEmail);
            seller = (Seller) sut.getUsuario(sellerEmail);
            
            File fotoBD = new File("imagen_falsa.jpg");
            Date manana = new Date(System.currentTimeMillis() + 86400000L);
            sale = sut.createSale(title, desc, 1, 100f, manana, sellerEmail, fotoBD);
            
            sut.createOferta(sale, 90f, buyer); 
            sut.close();

            testDA.open();
            offer = testDA.obtenerYAceptarOferta(buyerEmail);
            testDA.close();

        } catch (Exception e) {
            System.out.println("Error en setUp: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        try {
            testDA.open();
            testDA.removeOffer(offer);
            testDA.removeSale(sale);
            testDA.removeUsuario(buyerEmail);
            testDA.removeUsuario(sellerEmail);
            testDA.close();
        } catch (Exception e) {
            System.out.println("Error en tearDown: " + e.getMessage());
        }
    }

    @Test
    public void test1() {
        int expectedSize = 1;

        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(buyer, "BiciBD") ;
            sut.close();
            
            System.out.println(obtained);
            
            assertNotNull(obtained);
            assertEquals(expectedSize, obtained.size());
            assertEquals(buyerEmail, obtained.get(0).getBuyer().getEmail());
            assertEquals(1, obtained.get(0).getAccepted());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("White BD Test1");
        }
    }
    
    @Test
    public void test2() {
        try {
            sut.open();

            List<Offer> obtained = sut.getProductosComprados(buyer, null);
            
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
            
        } catch (Exception e) {
            fail("White BD Test2");
        }
    }

    @Test
    public void test3() {
        try {
            sut.open();
            
            List<Offer> obtained = sut.getProductosComprados(buyer, "UnTituloQueNoExiste");
            
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty()); 
            
        } catch (Exception e) {
            fail("White BD Test3");
        }
    }

    @Test
    public void test4() {
        try {
            testDA.open();
            testDA.marcarComoPendiente(offer); 
            testDA.close();

            sut.open();

            List<Offer> obtained = sut.getProductosComprados(buyer, title);
            
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty()); 
            
        } catch (Exception e) {
            fail("White BD Test4");
        }
    }
    
    @Test
    public void test5() {
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(null, title);
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
            
        } catch (Exception e) {
            fail("White BD Test5");
        }
    }

    @Test
    public void test6() {
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(buyer, null);
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
            
        } catch (Exception e) {
            fail("White BD Test6");
        }
    }
    
}
