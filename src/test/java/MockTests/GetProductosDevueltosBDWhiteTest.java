
package MockTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Offer;
import domain.Seller;
import testOperations.TestDataAccess;

public class GetProductosDevueltosBDWhiteTest {

	// sut: system under test
	static DataAccess sut = new DataAccess();

	// Operaciones auxiliares para alimentar y limpiar la BD real
	static TestDataAccess testDA = new TestDataAccess();

	private Seller seller;
	private String sellerMail;
	private String sellerName;

	@Before
	public void defaultValues() {
		sellerMail = "sellerTest@ehu.eus";
		sellerName = "Seller Test";
		seller = new Seller(sellerMail, sellerName, "1234");
	}

	@Test
	// sut.getProductosDevueltos: Camino 1 - No hay ofertas en la BD con ese titulo (offers vacio, bucle 0 iteraciones)
	public void test1() {
		String desc = "productoInexistenteEnBD";

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
	// sut.getProductosDevueltos: Camino 2 - Hay ofertas en la BD pero pertenecen a OTRO vendedor (primer if false)
	public void test2() {
		String desc = "balon";
		String otroSellerMail = "otroSeller@ehu.eus";

		testDA.open();
		testDA.addSellerWithSaleAndOffer(otroSellerMail, "Otro Vendedor", "futbol balon", "usado", -2);
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
	// sut.getProductosDevueltos: Camino 3 - El vendedor coincide pero accepted != -2 (segundo if false)
	public void test3() {
		String desc = "balon";

		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", 0); // accepted = 0
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
	// sut.getProductosDevueltos: Camino 4 - El vendedor coincide y accepted == -2 (ambos if true, anadido a res)
	public void test4() {
		String desc = "balon";

		testDA.open();
		testDA.addSellerWithSaleAndOffer(sellerMail, sellerName, "futbol balon", "usado", -2); // accepted = -2
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
}
