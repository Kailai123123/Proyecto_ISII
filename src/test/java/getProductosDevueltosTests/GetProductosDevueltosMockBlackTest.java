
package getProductosDevueltosTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

public class GetProductosDevueltosMockBlackTest {

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
	private String desc;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
		persistenceMock = Mockito.mockStatic(Persistence.class);
		persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
				.thenReturn(entityManagerFactory);

		Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
		Mockito.doReturn(et).when(db).getTransaction();

		Mockito.doReturn(query).when(db).createQuery(Mockito.anyString(), Mockito.eq(Offer.class));
		Mockito.doReturn(query).when(query).setParameter(Mockito.anyInt(), Mockito.any());

		sut = new DataAccess(db);

		sellerMail = "seller1@gmail.com";
		seller = Mockito.mock(Seller.class);
		Mockito.when(seller.getEmail()).thenReturn(sellerMail);

		desc = "balon";
	}

	@After
	public void tearDown() {
		persistenceMock.close();
	}

	@Test
	// CP1: se en BD, desc = "balon", oferta con accepted = -2 -> Retorna lista con oferta devuelta
	public void testCP1() {
		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(seller);
		Mockito.when(offerMock.getAccepted()).thenReturn(-2);
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

	@Test
	// CP2: se en BD, desc = "balon", no hay ventas coincidentes en BD -> Retorna lista vacia []
	public void testCP2() {
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
	// CP3: se en BD, desc = "balon", ofertas de OTRO vendedor -> Retorna lista vacia []
	public void testCP3() {
		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);
		Seller sellerOtro = Mockito.mock(Seller.class);
		Mockito.when(sellerOtro.getEmail()).thenReturn("otro@gmail.com");

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(sellerOtro);
		Mockito.when(offerMock.getAccepted()).thenReturn(-2);
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
	// CP4: se en BD, desc = "balon", oferta del vendedor pero accepted = 1 (o 0) -> Retorna lista vacia []
	public void testCP4() {
		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(seller);
		Mockito.when(offerMock.getAccepted()).thenReturn(1);
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
	// CP5: se = null, desc = "balon" -> Lanza NullPointerException al intentar se.getEmail()
	public void testCP5() {
		seller = null;
		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);
		Seller s1 = Mockito.mock(Seller.class);
		Mockito.when(s1.getEmail()).thenReturn("seller1@gmail.com");

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(s1);
		offers.add(offerMock);

		Mockito.when(query.getResultList()).thenReturn(offers);

		try {
			sut.open();
			sut.getProductosDevueltos(seller, desc);
			sut.close();
			fail("Deberia haber lanzado excepcion por vendedor nulo");

		} catch (NullPointerException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// CP6: se en BD, desc = "" -> Busqueda con cadena vacia (LIKE '%%') recupera ofertas devueltas
	public void testCP6() {
		desc = "";
		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(seller);
		Mockito.when(offerMock.getAccepted()).thenReturn(-2);
		offers.add(offerMock);

		Mockito.when(query.getResultList()).thenReturn(offers);

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(1, res.size());

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// CP7: se en BD, desc = null -> Sin coincidencias de busqueda -> Retorna lista vacia []
	public void testCP7() {
		desc = null;
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
	// CP8: se no existe registrado en BD / sin ofertas -> Retorna lista vacia []
	public void testCP8() {
		Seller sellerFake = Mockito.mock(Seller.class);
		Mockito.when(sellerFake.getEmail()).thenReturn("fake@gmail.com");

		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(seller); // La oferta es de seller1, no de sellerFake
		offers.add(offerMock);

		Mockito.when(query.getResultList()).thenReturn(offers);

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(sellerFake, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// CP9: Registro corrupto en BD (Venta sin vendedor asociado -> sale.getSeller() es null) -> Lanza NullPointerException
	public void testCP9() {
		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(null); // Corrupto: vendedor no asignado
		offers.add(offerMock);

		Mockito.when(query.getResultList()).thenReturn(offers);

		try {
			sut.open();
			sut.getProductosDevueltos(seller, desc);
			sut.close();
			fail("Deberia haber lanzado NullPointerException por integridad corrupta");

		} catch (NullPointerException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	// CP10: Oferta con accepted = -3 (fuera de rango) -> No es devuelta (-2) -> Retorna lista vacia []
	public void testCP10() {
		List<Offer> offers = new ArrayList<Offer>();
		Offer offerMock = Mockito.mock(Offer.class);
		Sale saleMock = Mockito.mock(Sale.class);

		Mockito.when(offerMock.getSale()).thenReturn(saleMock);
		Mockito.when(saleMock.getSeller()).thenReturn(seller);
		Mockito.when(offerMock.getAccepted()).thenReturn(-3);
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
}
