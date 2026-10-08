package testOperations;

import java.io.File;
import java.nio.file.Files;
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

public class TestDataAccess2 {
    
    protected EntityManager db;
    protected EntityManagerFactory emf;
    ConfigXML c = ConfigXML.getInstance();

    public TestDataAccess2() {
    }

    public void open() {
        String fileName = c.getDbFilename();
        try {
            File recoveryFile = new File(fileName + "$");
            if (recoveryFile.exists()) {
                Files.deleteIfExists(recoveryFile.toPath());
            }
        } catch (Exception e) {}

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
        if (db != null && db.isOpen()) db.close();
        if (emf != null && emf.isOpen()) emf.close();
    }
    
    public boolean removeOffer(Offer offer) {
        try {
            db.getTransaction().begin();
            Offer o = db.find(Offer.class, offer.getOfferId());
            if (o != null) db.remove(o);
            db.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeSale(Sale sale) {
        try {
            db.getTransaction().begin();
            Sale s = db.find(Sale.class, sale.getSaleNumber());
            if (s != null) db.remove(s);
            db.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeUsuario(String email) {
        try {
            db.getTransaction().begin();
            Buyer b = db.find(Buyer.class, email);
            if (b != null) db.remove(b);
            else {
                Seller s = db.find(Seller.class, email);
                if (s != null) db.remove(s);
            }
            db.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Offer obtenerYAceptarOferta(String buyerEmail) {
        db.getTransaction().begin();
        try {
            TypedQuery<Offer> q = db.createQuery("SELECT o FROM Offer o WHERE o.buyer.email = ?1", Offer.class);
            q.setParameter(1, buyerEmail);
            List<Offer> offers = q.getResultList();
            
            if (offers.isEmpty()) {
                db.getTransaction().rollback();
                return null;
            }
            
            Offer o = offers.get(offers.size() - 1);
            
            o.setAccepted(1);
            
            db.getTransaction().commit();
            return o;
            
        } catch (Exception e) {
            db.getTransaction().rollback();
            e.printStackTrace();
            return null;
        }
    }
    
    public void marcarComoPendiente(Offer offer) {
        db.getTransaction().begin();
        try {
            Offer o = db.find(Offer.class, offer.getOfferId());
            if (o != null) {
                o.setAccepted(0);
                db.merge(o);
            }
            db.getTransaction().commit();
        } catch (Exception e) {
            db.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    
    
}