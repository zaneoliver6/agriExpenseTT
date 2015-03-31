package uwi.dcit.agriexpensesvr;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

//https://cloud.google.com/appengine/docs/java/datastore/jpa/overview-dn2

public final class EMF {
    private static final EntityManagerFactory emfInstance = Persistence
            .createEntityManagerFactory("transactions-optional");

    private EMF() {
    }

    public static EntityManagerFactory get() {
        return emfInstance;
    }
}