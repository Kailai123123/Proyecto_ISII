
package getProductosDevueltosTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Offer;
import domain.Sale;
import domain.Seller;

public class GetProductosDevueltosMockWhiteTest {

	static DataAccess sut;

	protected MockedStatic<Persistence> persistenceMock;

	@Mock
	protected EntityManagerFactory entityManagerFactory;
	@Mock
	protected EntityManager db;
	@Mock
	protected EntityTransaction et;
	@Mock
	protected TypedQuery<Offer> query;

	private Seller seller;
	private String sellerMail;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
		persistenceMock = Mockito.mockStatic(Persistence.class);
		persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
				.thenReturn(entityManagerFactory);

		Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
		Mockito.doReturn(et).when(db).getTransaction();
		
		// Entrenar a db para que devuelva el mock query al llamar a createQuery
		Mockito.doReturn(query).when(db).createQuery(Mockito.anyString(), Mockito.eq(Offer.class));
		Mockito.doReturn(query).when(query).setParameter(Mockito.anyInt(), Mockito.any());

		sut = new DataAccess(db);

		// Mockeamos Seller para evitar problemas con sus constructores
		sellerMail = "sellerTest@ehu.eus";
		seller = Mockito.mock(Seller.class);
		Mockito.when(seller.getEmail()).thenReturn(sellerMail);
	}

	@After
	public void tearDown() {
		persistenceMock.close();
	}

	@Test
	// sut.getProductosDevueltos: La consulta no devuelve ofertas (offers vacio, bucle 0 iteraciones)
	public void test1() {
		String desc = "balon";
		List<Offer> offersVacia = new ArrayList<Offer>();
		Mockito.when(query.getResultList()).thenReturn(offersVacia);

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// sut.getProductosDevueltos: Hay ofertas pero el seller NO coincide (primer if evalua a false)
	public void test2() {
		String desc = "balon";
		List<Offer> offers = new ArrayList<Offer>();

		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);
		Seller sellerOtro = Mockito.mock(Seller.class);
		Mockito.when(sellerOtro.getEmail()).thenReturn("otro@ehu.eus");

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(sellerOtro);
		offers.add(offerMock);

		Mockito.when(query.getResultList()).thenReturn(offers);

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// sut.getProductosDevueltos: El seller coincide pero accepted != -2 (segundo if evalua a false)
	public void test3() {
		String desc = "balon";
		List<Offer> offers = new ArrayList<Offer>();

		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(seller);
		Mockito.when(offerMock.getAccepted()).thenReturn(0); // Diferente de -2
		offers.add(offerMock);

		Mockito.when(query.getResultList()).thenReturn(offers);

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// sut.getProductosDevueltos: El seller coincide y accepted == -2 (ambos if true, anadido a res)
	public void test4() {
		String desc = "balon";
		List<Offer> offers = new ArrayList<Offer>();

		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(seller);
		Mockito.when(offerMock.getAccepted()).thenReturn(-2); // Coincide con devuelto
		offers.add(offerMock);

		Mockito.when(query.getResultList()).thenReturn(offers);

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(1, res.size());
			assertEquals(offerMock, res.get(0));

		} catch (Exception e) {
			fail();
		}
	}
}
