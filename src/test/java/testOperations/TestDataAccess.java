/*
 * Esto lo he tenido q meter con chati pq no sabia como hacerlo sin el TestDataAccess
 * como lo hace el (lo de debajo es un resumen de chati de lo q ha tenido q cambiar comparado al de el profe)
 * 
 * La estructura es la misma que la del profe: usamos sut para probar el método, 
 * testDA para meter datos a la BD antes del test y el finally para borrar todo y 
 * dejar la base de datos limpia.
 * 
 * El único cambio es que el profe probaba crear ventas (createSale) y solo necesitaba 
 * guardar un vendedor y su venta. Nuestro método busca devoluciones (getProductosDevueltos),
 * así que necesitamos ofertas devueltas reales. Por eso añadimos en TestDataAccess el
 * método addSellerWithSaleAndOffer para meter a la vez el vendedor, la venta y la oferta
 * con su comprador y el accepted a -2 (o a 0), adaptamos el borrado para que elimine también
 * esas ofertas y usamos el constructor de Seller que pide contraseña.
 * 
 *  Además, para probar el caso CP9 (el de datos corruptos o error de integridad), metimos en 
 *  TestDataAccess el método addSaleWithoutSellerAndOffer. Lo que hace es guardar aposta en la 
 *  BD una venta «huérfana» sin ningún vendedor asociado (seller = null), para poder simular y 
 *  comprobar que el código lanza la excepción esperada (NullPointerException) cuando se encuentra 
 *  con un registro roto en la base de datos.
 *  
 */

package testOperations;

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

public class TestDataAccess {

	private EntityManager db;
	private EntityManagerFactory emf;
	ConfigXML c = ConfigXML.getInstance();

	public TestDataAccess() {
	}

	public void open() {
		String fileName = c.getDbFilename();
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
		System.out.println("TestDataAccess opened => isDatabaseLocal: " + c.isDatabaseLocal());
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
		System.out.println("TestDataAccess closed");
	}

	// Inserta vendedor, comprador, venta y oferta en la BD real para preparar el test
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
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		}
	}

	// Borra los datos creados en el test para dejar la BD limpia
	public boolean removeSeller(String sellerEmail) {
		db.getTransaction().begin();
		try {
			Seller seller = db.find(Seller.class, sellerEmail);
			if (seller != null) {
				TypedQuery<Offer> qOffers = db.createQuery(
						"SELECT o FROM Offer o WHERE o.sale.seller.email = ?1", Offer.class);
				qOffers.setParameter(1, sellerEmail);
				List<Offer> offers = qOffers.getResultList();
				for (Offer o : offers) {
					db.remove(o);
				}

				TypedQuery<Sale> qSales = db.createQuery(
						"SELECT s FROM Sale s WHERE s.seller.email = ?1", Sale.class);
				qSales.setParameter(1, sellerEmail);
				List<Sale> sales = qSales.getResultList();
				for (Sale s : sales) {
					db.remove(s);
				}

				db.remove(seller);
			}

			Buyer buyer = db.find(Buyer.class, "buyerTest@ehu.eus");
			if (buyer != null) {
				db.remove(buyer);
			}

			db.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			return false;
		}
	}
	
	// Inserta venta huerfana sin vendedor para probar CP9 (integridad corrupta)
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
		
}