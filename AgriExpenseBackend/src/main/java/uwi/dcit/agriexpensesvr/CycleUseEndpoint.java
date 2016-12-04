package uwi.dcit.agriexpensesvr;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

@Api(name = "cycleUseApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "agriexpensesvr.dcit.uwi",
                ownerName = "agriexpensesvr.dcit.uwi",
                packagePath = ""
        ))
public class CycleUseEndpoint {

    /**
     * This method lists all the entities inserted in datastore. It uses HTTP
     * GET method and paging support.
     *
     * @return A CollectionResponse class containing the list of all entities
     *         persisted and a cursor to the next page.
     */
    @SuppressWarnings({ "unchecked", "unused" })
    @ApiMethod(name = "listCycleUse")
    public CollectionResponse<CycleUse> listCycleUse(
            @Nullable @Named("cursor") String cursorString,
            @Nullable @Named("limit") Integer limit) {

        EntityManager mgr = null;
        Cursor cursor = null;
        List<CycleUse> execute = null;

        try {
            mgr = getEntityManager();
            Query query = mgr.createQuery("select from CycleUse as CycleUse");
            if (cursorString != null && cursorString != "") {
                cursor = Cursor.fromWebSafeString(cursorString);
                query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
            }

            if (limit != null) {
                query.setFirstResult(0);
                query.setMaxResults(limit);
            }

            execute = (List<CycleUse>) query.getResultList();
            cursor = JPACursorHelper.getCursor(execute);
            if (cursor != null)
                cursorString = cursor.toWebSafeString();

            // Tight loop for fetching all entities from datastore and
            // accomodate
            // for lazy fetch.
            for (CycleUse obj : execute)
                ;
        } finally {
            if (mgr != null)mgr.close();
        }

        return CollectionResponse.<CycleUse> builder().setItems(execute)
                .setNextPageToken(cursorString).build();
    }

    @ApiMethod(name = "getAllCycleUse")
    public List<CycleUse> getAllCycleUse(@Named("namespace") String namespace) {
        NamespaceManager.set(namespace);
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
                "CycleUse");

        PreparedQuery pq = datastore.prepare(q);
        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
        Iterator<Entity> i = results.iterator();
        List<CycleUse> cL = new ArrayList<CycleUse>();
        while (i.hasNext()) {
            Entity e = i.next();
            // System.out.println(e.toString());
            CycleUse c = new CycleUse();

            c.setId(Integer.parseInt("" + e.getProperty("id")));
            c.setCycleid(Integer.parseInt("" + e.getProperty("cycleid")));
            c.setPurchaseId(Integer.parseInt("" + e.getProperty("purchaseId")));
            c.setAmount((Double) e.getProperty("amount"));
            c.setCost((Double) e.getProperty("cost"));
            c.setResource((String) e.getProperty("resource"));
            c.setKeyrep((String) e.getProperty("keyrep"));
            cL.add(c);
        }
        return cL;
    }

    @ApiMethod(name = "deleteAll", httpMethod = HttpMethod.GET)
    public void deleteAll(@Named("namespace") String namespace) {
        NamespaceManager.set(namespace);
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
                "CycleUse");

        PreparedQuery pq = datastore.prepare(q);
        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
        Iterator<Entity> i = results.iterator();

        while (i.hasNext()) {
            String keyrep = (String) i.next().getProperty("keyrep");
            removeCycleUse(keyrep, namespace);
        }
    }

    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     *
     * //@param id
     *            the primary key of the java bean.
     * @return The entity with primary key id.
     */
    @ApiMethod(name = "getCycleUse")
    public CycleUse getCycleUse(@Named("namespace") String namespace,
                                @Named("keyrep") String keyrep) {
        NamespaceManager.set(namespace);
        EntityManager mgr = getEntityManager();
        CycleUse cycleuse = null;
        Key k = KeyFactory.stringToKey(keyrep);
        try {
            cycleuse = mgr.find(CycleUse.class, k);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cycleuse;
        // DatastoreService
        // datastore=DatastoreServiceFactory.getDatastoreService();
		/*
		 * Entity et = null; try { et=datastore.get(k); } catch
		 * (com.google.appengine.api.datastore.EntityNotFoundException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } if(et==null)
		 * return null; cycleuse.setAmount((Double) et.getProperty("amount"));
		 * cycleuse.setCost((Double) et.getProperty("cost"));
		 * cycleuse.setCycleid((Integer) et.getProperty("cycleid"));
		 * cycleuse.setPurchaseId((Integer) et.getProperty("purchaseId"));
		 * cycleuse.setResource((String) et.getProperty("resource")); /*try {
		 * cycleuse = mgr.find(CycleUse.class, id); } finally { mgr.close(); }
		 */
    }


    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     *
     * //@param id
     *            the primary key of the java bean.
     * @return The entity with primary key id.
     */
    @ApiMethod(name = "cycleUseWithId")
    public CycleUse cycleUseWithIdOnly(@Named("namespace") String namespace,
                                @Named("ID") int id) {
        NamespaceManager.set(namespace);
        EntityManager mgr = getEntityManager();
        CycleUse cycleuse = null;
        Key k = KeyFactory.createKey("CycleUse",id);
        String keyString = KeyFactory.keyToString(k);
        try {
            cycleuse = mgr.find(CycleUse.class, keyString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cycleuse;
        // DatastoreService
        // datastore=DatastoreServiceFactory.getDatastoreService();
		/*
		 * Entity et = null; try { et=datastore.get(k); } catch
		 * (com.google.appengine.api.datastore.EntityNotFoundException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } if(et==null)
		 * return null; cycleuse.setAmount((Double) et.getProperty("amount"));
		 * cycleuse.setCost((Double) et.getProperty("cost"));
		 * cycleuse.setCycleid((Integer) et.getProperty("cycleid"));
		 * cycleuse.setPurchaseId((Integer) et.getProperty("purchaseId"));
		 * cycleuse.setResource((String) et.getProperty("resource")); /*try {
		 * cycleuse = mgr.find(CycleUse.class, id); } finally { mgr.close(); }
		 */
    }



    /**
     * This inserts a new entity into App Engine datastore. If the entity
     * already exists in the datastore, an exception is thrown. It uses HTTP
     * POST method.
     *
     * @param cycleuse
     *            the entity to be inserted.
     * @return The inserted entity.
     */
    @ApiMethod(name = "insertCycleUse")
    public CycleUse insertCycleUse(CycleUse cycleuse) {
        // TODO
        NamespaceManager.set(cycleuse.getAccount());
        Key k = KeyFactory.createKey("CycleUse", cycleuse.getId());
        cycleuse.setKey(k);
        cycleuse.setKeyrep(KeyFactory.keyToString(k));
        EntityManager mgr = getEntityManager();
        try {
            if (containsCycleUse(cycleuse)) {
                throw new EntityExistsException("Object already exists ");
            }
            cycleuse.setKeyrep(KeyFactory.keyToString(k));
            cycleuse.setAccount(KeyFactory.keyToString(k));// using account to store
            // the string rep of the
            // key
            mgr.getTransaction().begin();
            mgr.persist(cycleuse);
            mgr.getTransaction().commit();
        } finally {
//            mgr.close();
        }
        return cycleuse;
    }

    /**
     * This method is used for updating an existing entity. If the entity does
     * not exist in the datastore, an exception is thrown. It uses HTTP PUT
     * method.
     *
     * @param cycleUse
     *            the entity to be updated.
     * @return The updated entity.
     */
    @ApiMethod(name = "updateCycleUse")
    public CycleUse updateCycleUse(CycleUse cycleUse){
        EntityManager mgr = getEntityManager();
        Key k = KeyFactory.stringToKey(cycleUse.getKeyrep());
        CycleUse findCycleUse = null;
        mgr.find(CycleUse.class,k);
        try {
            findCycleUse=mgr.find(CycleUse.class,k);
            if (findCycleUse==null) {
                throw new EntityNotFoundException("Object does not exist   ");
            }
            else{
                if(cycleUse.getResource()!=null)
                    findCycleUse.setResource(cycleUse.getResource());
                if(cycleUse.getAmount()!=0)
                    findCycleUse.setAmount(cycleUse.getAmount());
                mgr.getTransaction().begin();
                mgr.persist(findCycleUse);
                mgr.getTransaction().commit();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
//            mgr.close();
        }
        return findCycleUse;
    }

    /**
     * This method removes the entity with primary key id. It uses HTTP DELETE
     * method.
     *
     * //@param id
     *            the primary key of the entity to be deleted.
     */
    @ApiMethod(name = "removeCycleUse", httpMethod = HttpMethod.DELETE)
    public void removeCycleUse(@Named("keyrep") String keyrep,  @Named("namespace") String namespace) {
        NamespaceManager.set(namespace);
//        DatastoreService d = DatastoreServiceFactory.getDatastoreService();
//        Key k = KeyFactory.stringToKey(keyrep);
//        try {
//            d.delete(k);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        EntityManager mgr = getEntityManager();
//        CycleUse findCycleUse=mgr.find(CycleUse.class,keyrep);
        try{
//            CycleUse findCycleUse=mgr.find(CycleUse.class,keyrep);
            CycleUse findCycleUse = mgr.find(CycleUse.class,KeyFactory.stringToKey(keyrep));
            mgr.getTransaction().begin();
            mgr.remove(findCycleUse);
            mgr.getTransaction().commit();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean containsCycleUse(CycleUse cycleuse) {
        NamespaceManager.set(cycleuse.getAccount());
        EntityManager mgr = getEntityManager();
        boolean contains = true;
        try {
            CycleUse item = mgr.find(CycleUse.class, cycleuse.getKey());
            if (item == null) {
                contains = false;
            }
        } finally {
//            mgr.close();
        }
        return contains;
    }

    private static EntityManager getEntityManager() {
        //return uwi.dcit.agriexpensesvr.EMF.get().createEntityManager();
        return EMF.getManagerInstance();
    }

}
