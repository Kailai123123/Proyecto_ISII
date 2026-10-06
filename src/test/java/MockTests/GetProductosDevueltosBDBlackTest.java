package MockTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Offer;
import domain.Seller;
import testOperations.TestDataAccess;

public class GetProductosDevueltosBDBlackTest {

	// sut: system under test
	static DataAccess sut = new DataAccess();

	// Operaciones auxiliares para preparar y limpiar la BD real
	static TestDataAccess testDA = new TestDataAccess();

	private Seller seller;
	private String sellerMail;
	private String sellerName;
	private String desc;

	@Before
	public void defaultValues() {
		sellerMail = "seller1@gmail.com";
		sellerName = "Aitor";
		seller = new Seller(sellerMail, sellerName, "123");
		desc = "balon";
	}

	@Test
	// CP1: se en BD, desc = "balon", oferta con accepted = -2 -> Retorna lista con oferta devuelta
	public void testCP1() {
		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(1, res.size());
			assertEquals(-2, res.get(0).getAccepted());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP2: se en BD, desc = "balon", no hay ventas con ese titulo en BD -> Retorna lista vacia []
	public void testCP2() {
		desc = "productoInexistente";

		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP3: se en BD, desc = "balon", ofertas pertenecen a OTRO vendedor -> Retorna lista vacia []
	public void testCP3() {
		String otroSellerMail = "otro@gmail.com";

		testDA.open();
		testDA.addSellerWithSaleAndOffer(otroSellerMail, "Ane", "futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(otroSellerMail);
			testDA.close();
		}
	}

	@Test
	// CP4: se en BD, desc = "balon", oferta del vendedor pero accepted = 1 -> Retorna lista vacia []
	public void testCP4() {
		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", 1); // accepted = 1
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP5: se = null, desc = "balon" -> Lanza NullPointerException al evaluar getEmail()
	public void testCP5() {
		seller = null;

		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			sut.getProductosDevueltos(seller, desc);
			sut.close();
			fail("Deberia haber lanzado NullPointerException");

		} catch (NullPointerException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP6: se en BD, desc = "" -> Cadena vacia busca cualquier titulo (LIKE '%%') y devuelve ofertas
	public void testCP6() {
		desc = "";

		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(1, res.size());
			assertEquals(-2, res.get(0).getAccepted());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP7: se en BD, desc = null -> No coincide con el titulo en BD -> Retorna lista vacia []
	public void testCP7() {
		desc = null;

		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP8: se no existe registrado en BD / es otro vendedor -> Retorna lista vacia []
	public void testCP8() {
		Seller sellerFake = new Seller("fake@gmail.com", "Fake Seller", "123");

		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(sellerFake, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP9: Registro corrupto en BD (Venta sin vendedor) -> Lanza NullPointerException
	public void testCP9() {
		testDA.open();
		testDA.addSaleWithoutSellerAndOffer("futbol balon", "usado", -2);
		testDA.close();

		try {
			sut.open();
			sut.getProductosDevueltos(seller, desc);
			sut.close();
			fail("Deberia haber lanzado NullPointerException por venta sin vendedor");

		} catch (NullPointerException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}

	@Test
	// CP10: se en BD, desc = "balon", oferta con accepted = -3 (fuera de rango) -> Retorna lista vacia []
	public void testCP10() {
		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -3); // accepted = -3
		testDA.close();

		try {
			sut.open();
			List<Offer> res = sut.getProductosDevueltos(seller, desc);
			sut.close();

			assertNotNull(res);
			assertEquals(0, res.size());

		} catch (Exception e) {
			fail();
		} finally {
			testDA.open();
			testDA.removeSeller(sellerMail);
			testDA.close();
		}
	}
}