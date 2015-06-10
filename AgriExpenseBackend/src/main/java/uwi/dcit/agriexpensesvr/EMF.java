package uwi.dcit.agriexpensesvr;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

//https://cloud.google.com/appengine/docs/java/datastore/jpa/overview-dn2

public final class EMF {
    private static final EntityManagerFactory emfInstance = Persistence
            .createEntityManagerFactory("transactions-optional");
    private static EntityManager entityManager;


    private EMF() {
    }

    /*
    public static EntityManagerFactory get() {
        return emfInstance;
    }
    */
    public static EntityManager getManagerInstance() {
        if (entityManager == null) entityManager = emfInstance.createEntityManager();
        return entityManager;
    }
}