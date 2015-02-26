package uwi.dcit.agriexpensesvr;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.logging.Logger;

import javax.inject.Named;
import javax.persistence.EntityManager;

@Api(name = "accNameApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "agriexpensesvr.dcit.uwi",
                ownerName = "agriexpensesvr.dcit.uwi",
                packagePath = ""
        ))
public class AccNameEndpoint {

    private static final Logger logger = Logger.getLogger(AccNameEndpoint.class.getName());

    /**
     * This method gets the <code>AccName</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>AccName</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getAccName")
    public AccName getAccName(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getAccName method");
        return null;
    }

    /**
     * This inserts a new <code>AccName</code> object.
     *
     * @param accName The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertAccName")
    public AccName insertAccName(AccName accName) {
        // TODO: Implement this function
        int count=1;

        EntityManager mgr = getEntityManager();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter nameFilter = new Query.FilterPredicate("account", Query.FilterOperator.EQUAL, accName.getAccount());
        while(count>0) {
            Query q = new Query("AccName").setFilter(nameFilter);
            PreparedQuery pq = datastore.prepare(q);
            count=pq.countEntities();
            nameFilter = new Query.FilterPredicate("account", Query.FilterOperator.EQUAL, accName.getAccount()+count);
        }
        accName.setAccount(accName.getAccount()+count);

        return accName;
    }
    private static EntityManager getEntityManager() {
        return EMF.get().createEntityManager();
    }
}