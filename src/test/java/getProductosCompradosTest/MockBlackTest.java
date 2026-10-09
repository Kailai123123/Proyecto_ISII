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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MockBlackTest {

    static DataAccess sut;
    protected MockedStatic<Persistence> persistenceMock;

    @Mock 
    protected EntityManagerFactory emf;
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
    @Mock 
    protected Offer mockOffer2;

    private String validEmail = "comprador@mail.com";

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(emf);
        Mockito.doReturn(db).when(emf).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        
        sut = new DataAccess(db);

        Mockito.when(mockBuyer.getEmail()).thenReturn(validEmail);
        String expectedQuery = "SELECT o FROM Offer o JOIN o.sale s WHERE s.title LIKE ?1";
        Mockito.when(db.createQuery(expectedQuery, Offer.class)).thenReturn(typedQueryOffer);
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }
/*
    @Test
    public void test1() {
        try {
            sut.open();
            List<Offer> result = sut.getProductosComprados(null, "bicicleta");
            sut.close();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        } catch (Exception e) {
        	fail("Black Mock Test1");
        }
    }

    @Test
    public void test2() {
        try {
            sut.open();
            List<Offer> result = sut.getProductosComprados(mockBuyer, null);
            sut.close();
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
        } catch (Exception e) {
        	fail("Black Mock Test2");
        }
    }

    @Test
    public void test3() {
        Mockito.when(typedQueryOffer.getResultList()).thenReturn(Collections.emptyList());

        try {
            sut.open();
            List<Offer> result = sut.getProductosComprados(mockBuyer, "producto_inexistente");
            sut.close();
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
        } catch (Exception e) {
            fail("Black Mock Test3");
        }
    }

    @Test
    public void test4() {
        Mockito.when(mockOffer.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer.getAccepted()).thenReturn(0);       
        Mockito.when(typedQueryOffer.getResultList()).thenReturn(Collections.singletonList(mockOffer));

        try {
            sut.open();
            List<Offer> result = sut.getProductosComprados(mockBuyer, "bicicleta");
            sut.close();
            
            assertTrue(result.isEmpty());
        } catch (Exception e) {
            fail("Black Mock Test4");
        }
    }
*/
    @Test
    public void test5() {
        Mockito.when(mockOffer.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer.getAccepted()).thenReturn(1);         
        Mockito.when(typedQueryOffer.getResultList()).thenReturn(Collections.singletonList(mockOffer));

        try {
            sut.open();
            List<Offer> result = sut.getProductosComprados(mockBuyer, "bicicleta");
            sut.close();

            assertEquals(1, result.size());
            assertEquals(mockBuyer, result.get(0).getBuyer());
            assertEquals(1, result.get(0).getAccepted());
        } catch (Exception e) {
            fail("Black Mock Test5");
        }
    }
    
    @Test
    public void test6() {
    	int expected = 2;
        Mockito.when(mockOffer.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer.getAccepted()).thenReturn(1);
        
        Mockito.when(mockOffer2.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer2.getAccepted()).thenReturn(1);

        Mockito.when(typedQueryOffer.getResultList()).thenReturn(Arrays.asList(mockOffer, mockOffer2));

        try {
            sut.open();
            List<Offer> result = sut.getProductosComprados(mockBuyer, "bicicleta");
            sut.close();

            assertNotNull(result);
            assertEquals(expected, result.size());
            assertEquals(1, result.get(0).getAccepted());
            assertEquals(1, result.get(1).getAccepted());
        } catch (Exception e) {
            fail("Black Mock Test6");
        }
    }

    @Test
    public void test7() {
    	int expected = 2;
        Mockito.when(db.createQuery(Mockito.anyString(), Mockito.eq(Offer.class))).thenReturn(typedQueryOffer);      
        Mockito.when(mockOffer.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer.getAccepted()).thenReturn(1);
        Mockito.when(mockOffer2.getBuyer()).thenReturn(mockBuyer);
        Mockito.when(mockOffer2.getAccepted()).thenReturn(1);

        Mockito.when(typedQueryOffer.getResultList()).thenReturn(Arrays.asList(mockOffer, mockOffer2));

        try {
            sut.open();
            List<Offer> result = sut.getProductosComprados(mockBuyer, "");
            sut.close();

            assertNotNull(result);
            assertEquals(expected, result.size());
        } catch (Exception e) {
            fail("Black Mock Test7");
        }
    }
    
}