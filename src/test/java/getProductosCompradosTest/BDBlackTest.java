package getProductosCompradosTest;

import domain.Buyer;
import domain.Offer;
import domain.Sale;
import domain.Seller;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import testOperations.TestDataAccess2;

import java.io.File;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
/*
public class BDBlackTest {

    static DataAccess sut;
    static TestDataAccess2 testDA;

    private String buyerEmail = "compradorBD_black@mail.com";
    private String sellerEmail = "vendedorBD_black@mail.com";
    private String title = "BiciBD";
    private String desc = "Bicicleta BD test para Caja Negra";
    
    private Buyer buyer;
    private Seller seller;
    private Sale sale;
    private Offer offer;

    @Before
    public void setUp() {
        testDA = new TestDataAccess2();
        testDA.open();
        testDA.removeUsuario(buyerEmail);
        testDA.removeUsuario(sellerEmail);
        testDA.close();

        sut = new DataAccess();

        try {
            sut.open();
            sut.crearUsuario(buyerEmail, "123", "Buyer", "Black Test Buyer");
            sut.crearUsuario(sellerEmail, "123", "Seller", "Black Test Seller");
            
            buyer = (Buyer) sut.getUsuario(buyerEmail);
            seller = (Seller) sut.getUsuario(sellerEmail);
            
            Date manana = new Date(System.currentTimeMillis() + 86400000L);
            File fotoBD = new File("imagen_falsa_cajanegra.jpg");
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
            if (offer != null) testDA.removeOffer(offer);
            if (sale != null) testDA.removeSale(sale);
            testDA.removeUsuario(buyerEmail);
            testDA.removeUsuario(sellerEmail);
            testDA.close();
        } catch (Exception e) {
            System.out.println("Error en tearDown: " + e.getMessage());
        }
    }

    @Test
    public void test1() {
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(null, title);
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
        } catch (Exception e) {
            fail("Black BD Test1");
        }
    }

    @Test
    public void test2() {
        try {
            sut.open();
            Buyer buyerConectado = (Buyer) sut.getUsuario(buyerEmail);
            List<Offer> obtained = sut.getProductosComprados(buyerConectado, null);
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
        } catch (Exception e) {
            fail("Black BD Test2");
        }
    }

    @Test
    public void test3() {
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(buyer, "CocheInexistente");
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
        } catch (Exception e) {
            fail("Black BD Test3");
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
            fail("Black BD Test4");
        }
    }

    @Test
    public void test5() {
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(buyer, title);
            sut.close();
            
            assertNotNull(obtained);
            assertEquals(1, obtained.size());
            assertEquals(buyerEmail, obtained.get(0).getBuyer().getEmail());
            assertEquals(1, obtained.get(0).getAccepted());
        } catch (Exception e) {
            fail("Black BD Test5");
        }
    }
    
    @Test
    public void test6() {
        try {
            sut.open();
            Date manana = new Date(System.currentTimeMillis() + 86400000L);
            File fotoBD2 = new File("imagen.jpg");
            Sale sale2 = sut.createSale(title + " 2", "Segunda bici para CP6", 1, 200f, manana, sellerEmail, fotoBD2);
            sut.createOferta(sale2, 180f, buyer);
            sut.close();

            testDA.open();
            testDA.obtenerYAceptarOferta(buyerEmail);
            testDA.close();

            sut.open();
            List<Offer> obtained = sut.getProductosComprados(buyer, title);
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.size() >= 2);
            
        } catch (Exception e) {
            fail("Black BD Test6");
        }
    }

    @Test
    public void test7() {
        try {
            sut.open();
            Date manana = new Date(System.currentTimeMillis() + 86400000L);
            File fotoBD2 = new File("imagen.jpg");
            Sale saleDistinta = sut.createSale("PatineteBD", "Un patinete para CP7", 1, 150f, manana, sellerEmail, fotoBD2);
            sut.createOferta(saleDistinta, 100f, buyer);
            sut.close();

            testDA.open();
            testDA.obtenerYAceptarOferta(buyerEmail);
            testDA.close();

            sut.open();
            List<Offer> obtained = sut.getProductosComprados(buyer, "");
            sut.close();
            
            assertNotNull(obtained);
            assertTrue("Al buscar con cadena vacía, debería devolver todos los productos", obtained.size() >= 2);
            
        } catch (Exception e) {
            fail("Black BD Test7");
        }
    }
}
*/