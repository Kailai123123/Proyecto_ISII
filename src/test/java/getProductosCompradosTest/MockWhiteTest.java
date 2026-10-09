package getProductosCompradosTest;

import domain.Buyer;
import domain.Offer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import dataAccess.DataAccess;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MockWhiteTest {

	static DataAccess sut;
    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db;
    @Mock
    protected EntityTransaction et;
    @Mock
    protected TypedQuery<Offer> typedQueryOffer;
    @Mock
    protected Buyer mockBuyer;
    @Mock
    protected Offer mockOffer;

    private String buyerMail;
    private String desc;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);
        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();

        sut = new DataAccess(db);
        buyerMail = "buyer1@mail.com";
        desc = "bicicleta";
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test
    public void test1() {
        int expectedSize = 1;

        Mockito.when(mockBuyer.getEmail()).thenReturn(buyerMail);
        Mockito.when(mockOffer.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer.getAccepted()).thenReturn(1);

        Mockito.when(db.createQuery("SELECT o FROM Offer o JOIN o.sale s WHERE s.title LIKE ?1", Offer.class))
               .thenReturn(typedQueryOffer);         
        Mockito.when(typedQueryOffer.getResultList())
               .thenReturn(Collections.singletonList(mockOffer));

        try {
            sut.open();
            List<Offer> res = sut.getProductosComprados(mockBuyer, desc);
            sut.close();

            assertEquals(expectedSize, res.size());
            assertTrue(res.contains(mockOffer));    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            fail("White Mock test1");
        }
    }
    
    @Test
    public void test2() {
        int expectedSize = 0;

        Mockito.when(mockBuyer.getEmail()).thenReturn(buyerMail);
        
        Mockito.when(db.createQuery("SELECT o FROM Offer o JOIN o.sale s WHERE s.title LIKE ?1", Offer.class))
               .thenReturn(typedQueryOffer);              
        Mockito.when(typedQueryOffer.getResultList())
               .thenReturn(Collections.emptyList());
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(mockBuyer, desc);
            sut.close();

            assertEquals(expectedSize, obtained.size());
            assertTrue(obtained.isEmpty());       
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            fail("White Mock test2");
        }
    
    }
    
    @Test
    public void test3() {
        int expectedSize = 0;
        Mockito.when(mockBuyer.getEmail()).thenReturn(buyerMail);
        Buyer mockOtroComprador = Mockito.mock(Buyer.class);
        Mockito.when(mockOtroComprador.getEmail()).thenReturn("otro@mail.com");
        
        Mockito.when(mockOffer.getBuyer()).thenReturn(mockOtroComprador);
        
        Mockito.when(db.createQuery("SELECT o FROM Offer o JOIN o.sale s WHERE s.title LIKE ?1", Offer.class))
               .thenReturn(typedQueryOffer); 
        Mockito.when(typedQueryOffer.getResultList())
               .thenReturn(Collections.singletonList(mockOffer));

        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(mockBuyer, desc);
            sut.close();

            assertEquals(expectedSize, obtained.size());    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            fail("White Mock test3");
        }
    	
    }
    
    @Test
    public void test4() {
        int expectedSize = 0;
        
        Mockito.when(mockBuyer.getEmail()).thenReturn(buyerMail);
        
        Mockito.when(mockOffer.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer.getAccepted()).thenReturn(0); 

        Mockito.when(db.createQuery("SELECT o FROM Offer o JOIN o.sale s WHERE s.title LIKE ?1", Offer.class))
               .thenReturn(typedQueryOffer);          
        Mockito.when(typedQueryOffer.getResultList())
               .thenReturn(Collections.singletonList(mockOffer));

        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(mockBuyer, desc);
            sut.close();

            assertEquals(expectedSize, obtained.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            fail("White Mock Test4");
        }
    	
    }
    
    @Test
    public void test5() {
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(null, "bicicleta");
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
            
        } catch (Exception e) {
            fail("White Mock Test5");
        }
    }

    @Test
    public void test6() {
        try {
            sut.open();
            List<Offer> obtained = sut.getProductosComprados(mockBuyer, null);
            sut.close();
            
            assertNotNull(obtained);
            assertTrue(obtained.isEmpty());
            
        } catch (Exception e) {
            fail("White Mock Test6");
        }
    }
    
}