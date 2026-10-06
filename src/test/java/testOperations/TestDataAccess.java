package testOperations;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import domain.Buyer;
import domain.Offer;
import domain.Sale;
import domain.Seller;

/**
 *            ¡¡¡¡¡¡ESTO ME LO HA HECHO CHATI PQ TENIA Q HACERLO 
 *                   CON EL TESTDATAACCESS PERO NO ME SALIA BIEN COPIANDO EL SUYO!!!!!!!!!
 * 
 * =====================================================================================
 * EXPLICACIÓN DE CAMBIOS RESPECTO A LA PLANTILLA DEL PROFESOR
 * =====================================================================================
 * 
 * 1. ¿Por qué usamos esta clase?
 *    El profesor la utiliza para insertar y limpiar datos en la base de datos real
 *    sin mezclar métodos "sucios" de prueba dentro de la lógica de negocio (DataAccess).
 * 
 * 2. addSellerWithSaleAndOffer(...):
 *    El profesor en su ejemplo solo creaba vendedores y ventas (createSale). Como nuestro
 *    método (getProductosDevueltos) busca ofertas devueltas, tuvimos que crear este método
 *    para insertar toda la jerarquía junta: Seller -> Sale -> Offer (con su Buyer) y fijar
 *    el valor de 'accepted' (-2 para devuelto, 0 para pendiente, 1 para aceptado).
 * 
 * 3. addSaleWithoutSellerAndOffer(...):
 *    Creado específicamente para el caso de prueba CP9 (integridad/datos corruptos). Guarda
 *    una venta huérfana (seller = null) para verificar que el código lanza NullPointerException.
 * 
 * 4. removeSeller(...) mejorado:
 *    El profesor solo borraba ventas del vendedor. Aquí borramos en cascada las ofertas,
 *    las ventas y el vendedor. Además, añadimos una limpieza explícita de cualquier venta
 *    con título "futbol balon" para que la venta huérfana de CP9 no se quede en disco y no
 *    haga fallar los tests que van detrás (CP10, BDWhiteTest, etc.).
 * 
 * 5. Limpieza automática de .temp y .temp$ en open():
 *    Añadido para evitar el bloqueo típico de ObjectDB ("Recovery file does not match db file")
 *    sin tener que ir a Windows a borrar los archivos temporales a mano.
 * =====================================================================================
 */

public class TestDataAccess {

	private EntityManager db;
	private EntityManagerFactory emf;
	ConfigXML c = ConfigXML.getInstance();

	public TestDataAccess() {
	}

	public void open() {
		String fileName = c.getDbFilename();

		// LIMPIEZA AUTOMÁTICA: Si quedó un archivo de recuperación temporal .temp$
		// colgado de un test previo o un cierre abrupto, lo eliminamos antes de abrir la BD
		try {
			File recoveryFile = new File(fileName + "$");
			if (recoveryFile.exists()) {
				Files.deleteIfExists(recoveryFile.toPath());
				System.out.println(">> TestDataAccess: Archivo temporal de recuperación (" + fileName + "$) eliminado con éxito.");
			}
		} catch (Exception e) {
				// Si está bloqueado durante la ejecución, se ignora y continúa
		}

		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("javax.persistence.jdbc.user", c.getUser());
			properties.put("javax.persistence.jdbc.password", c.getPassword());

			emf = Persistence.createEntityManagerFactory(
					"objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, properties);
			db = emf.createEntityManager();
		}
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	// Inserta un escenario completo: Vendedor + Venta + Oferta (con Buyer asociado)
	public Offer addSellerWithSaleAndOffer(String sellerEmail, String sellerName, String saleTitle, String saleDesc, int accepted) {
		db.getTransaction().begin();
		try {
			Seller seller = db.find(Seller.class, sellerEmail);
			if (seller == null) {
				seller = new Seller(sellerEmail, sellerName, "1234");
				db.persist(seller);
			}

			String buyerEmail = "buyerTest@ehu.eus";
			Buyer buyer = db.find(Buyer.class, buyerEmail);
			if (buyer == null) {
				buyer = new Buyer(buyerEmail, "Buyer Test", "1234");
				db.persist(buyer);
			}

			Sale sale = seller.addSale(saleTitle, saleDesc, 0, 10f, new Date(), null);
			db.persist(sale);

			Offer offer = new Offer(buyer, 10f, sale);
			offer.setAccepted(accepted);
			db.persist(offer);

			db.getTransaction().commit();
			return offer;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
			return null;
		}
	}

	// Inserta una venta huérfana sin vendedor para probar CP9 (integridad de datos corrupta)
	public Offer addSaleWithoutSellerAndOffer(String saleTitle, String saleDesc, int accepted) {
		db.getTransaction().begin();
		try {
			String buyerEmail = "buyerTest@ehu.eus";
			Buyer buyer = db.find(Buyer.class, buyerEmail);
			if (buyer == null) {
				buyer = new Buyer(buyerEmail, "Buyer Test", "1234");
				db.persist(buyer);
			}

			Sale sale = new Sale(saleTitle, saleDesc, 0, 10f, new Date(), null, null);
			db.persist(sale);

			Offer offer = new Offer(buyer, 10f, sale);
			offer.setAccepted(accepted);
			db.persist(offer);

			db.getTransaction().commit();
			return offer;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			return null;
		}
	}

	// Limpia la BD después de cada test garantizando que no queden datos residuales
	public boolean removeSeller(String sellerEmail) {
		db.getTransaction().begin();
		try {
			Seller seller = db.find(Seller.class, sellerEmail);
			if (seller != null) {
				TypedQuery<Offer> qOffers = db.createQuery(
						"SELECT o FROM Offer o WHERE o.sale.seller.email = ?1", Offer.class);
				qOffers.setParameter(1, sellerEmail);
				List<Offer> offers = qOffers.getResultList();
				for (Offer o : offers) db.remove(o);

				TypedQuery<Sale> qSales = db.createQuery(
						"SELECT s FROM Sale s WHERE s.seller.email = ?1", Sale.class);
				qSales.setParameter(1, sellerEmail);
				List<Sale> sales = qSales.getResultList();
				for (Sale s : sales) db.remove(s);

				db.remove(seller);
			}

			// LIMPIEZA EXTRA: Barrido de ofertas y ventas con título de prueba que pudieran ser huérfanas
			TypedQuery<Offer> qTestOffers = db.createQuery(
					"SELECT o FROM Offer o WHERE o.sale.title = 'futbol balon'", Offer.class);
			for (Offer o : qTestOffers.getResultList()) db.remove(o);

			TypedQuery<Sale> qTestSales = db.createQuery(
					"SELECT s FROM Sale s WHERE s.title = 'futbol balon'", Sale.class);
			for (Sale s : qTestSales.getResultList()) db.remove(s);

			Buyer buyer = db.find(Buyer.class, "buyerTest@ehu.eus");
			if (buyer != null) db.remove(buyer);

			db.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			e.printStackTrace();
			return false;
		}
	}
}