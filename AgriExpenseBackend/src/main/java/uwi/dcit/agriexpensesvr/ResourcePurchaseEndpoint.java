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
import com.google.appengine.api.datastore.Entities;
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
import javax.annotation.Resource;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

@Api(name = "resourcePurchaseApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "agriexpensesvr.dcit.uwi",
                ownerName = "agriexpensesvr.dcit.uwi",
                packagePath = ""
        ))
public class ResourcePurchaseEndpoint {

    /**
     * This method lists all the entities inserted in datastore. It uses HTTP
     * GET method and paging support.
     *
     * @return A CollectionResponse class containing the list of all entities
     *         persisted and a cursor to the next page.
     */
    @SuppressWarnings({ "unchecked", "unused" })
    @ApiMethod(name = "listRPurchase")
    public CollectionResponse<ResourcePurchase> listRPurchase(
            @Nullable @Named("cursor") String cursorString,
            @Nullable @Named("limit") Integer limit) {

        EntityManager mgr = null;
        Cursor cursor = null;
        List<ResourcePurchase> execute = null;

        try {
            mgr = getEntityManager();
            Query query = mgr.createQuery("select from ResourcePurchase as ResourcePurchase");
            if (cursorString != null && cursorString != "") {
                cursor = Cursor.fromWebSafeString(cursorString);
                query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
            }

            if (limit != null) {
                query.setFirstResult(0);
                query.setMaxResults(limit);
            }

            execute = (List<ResourcePurchase>) query.getResultList();
            cursor = JPACursorHelper.getCursor(execute);
            if (cursor != null)
                cursorString = cursor.toWebSafeString();

            // Tight loop for fetching all entities from datastore and
            // accomodate
            // for lazy fetch.
            for (ResourcePurchase obj : execute)
                ;
        } finally {
//            mgr.close();
        }

        return CollectionResponse.<ResourcePurchase> builder().setItems(execute)
                .setNextPageToken(cursorString).build();
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "fetchAllPurchases")
    public List<ResourcePurchase> fetchAllPurchases() {

        EntityManager mgr = null;
        List<ResourcePurchase> execute = null;
        Query query = null;

		/* For namespace list fetching */
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
                Entities.NAMESPACE_METADATA_KIND);

        List<String> results = new ArrayList<String>();
        for (Entity e : ds.prepare(q).asIterable()) {
            if (e.getKey().getId() != 0) {
                System.out.println("<default>");
            } else {
                // System.out.println(e.getKey().getName());
                results.add(Entities.getNamespaceFromNamespaceKey(e.getKey()));
            }
        }
        mgr = getEntityManager();
        query = mgr.createQuery("SELECT FROM ResourcePurchase AS ResourcePurchase");

        // Set each namespace then return all results under that given namespace

        List<ResourcePurchase> purchaseList = new ArrayList<ResourcePurchase>();

        for (String i : results) {

            NamespaceManager.set(i);
            execute = (List<ResourcePurchase>) query.getResultList();
            for (ResourcePurchase obj : execute) {
                purchaseList.add(obj);
            }
        }
        return purchaseList;
    }

    @ApiMethod(name = "getAllPurchases")
    public List<ResourcePurchase> getAllPurchases(@Named("namespace") String namespace) {
        NamespaceManager.set(namespace);
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
                "ResourcePurchase");

        PreparedQuery pq = datastore.prepare(q);
        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
        Iterator<Entity> i = results.iterator();
        List<ResourcePurchase> pL = new ArrayList<ResourcePurchase>();
        System.out.println("record Holder:------------------");
        while (i.hasNext()) {
            System.out.println("record------------------");
            Entity e = i.next();
            System.out.println(e.toString());
            ResourcePurchase p = new ResourcePurchase();

            p.setpId(Integer.parseInt("" + e.getProperty("pId")));
            p.setType((String) e.getProperty("type"));
            p.setResourceId(Integer.parseInt("" + e.getProperty("resourceId")));
            p.setQuantifier((String) e.getProperty("quantifier"));
            p.setQty((Double) e.getProperty("qty"));
            p.setCost((Double) e.getProperty("cost"));
            p.setQtyRemaining((Double) e.getProperty("qtyRemaining"));
            p.setKeyrep((String) e.getProperty("keyrep"));
            pL.add(p);
        }
        return pL;
    }

    @ApiMethod(name = "deleteAll", httpMethod = HttpMethod.GET)
    public void deleteAll(@Named("namespace") String namespace) {
        NamespaceManager.set(namespace);
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
                "ResourcePurchase");

        PreparedQuery pq = datastore.prepare(q);
        List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
        Iterator<Entity> i = results.iterator();

        while (i.hasNext()) {
            String keyrep = (String) i.next().getProperty("keyrep");
            removeRPurchase(keyrep, namespace);
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
    @ApiMethod(name = "getRPurchase")
    public ResourcePurchase getRPurchase(@Named("namespace") String namespace,
                                  @Named("keyrep") String keyrep) {
        NamespaceManager.set(namespace);
        Key k = KeyFactory.stringToKey(keyrep);
        EntityManager mgr = getEntityManager();
        ResourcePurchase rpurchase = null;
        try {
            rpurchase = mgr.find(ResourcePurchase.class, k);
        } finally {
//            mgr.close();
        }
        System.out.println("---000---");
        return rpurchase;
		/*
		 * DatastoreService
		 * datastore=DatastoreServiceFactory.getDatastoreService(); Key
		 * k=KeyFactory.stringToKey(id); Entity et = null; try {
		 * et=datastore.get(k); } catch
		 * (com.google.appengine.api.datastore.EntityNotFoundException e) {
		 * 
		 * e.printStackTrace(); } RPurchase p=new RPurchase(); if(et==null){
		 * return null; }
		 * 
		 * p.setCost((Double) et.getProperty("cost")); p.setQty((Double)
		 * et.getProperty("qty")); p.setQuantifier((String)
		 * et.getProperty("quantifier")); p.setResourceId((Integer)
		 * et.getProperty("resourceId")); p.setQtyRemaining((Double)
		 * et.getProperty("qtyRemaining")); p.setType((String)
		 * et.getProperty("type")); return p;
		 */
    }

    /**
     * This inserts a new entity into App Engine datastore. If the entity
     * already exists in the datastore, an exception is thrown. It uses HTTP
     * POST method.
     *
     * @param rpurchase
     *            the entity to be inserted.
     * @return The inserted entity.
     */
    @ApiMethod(name = "insertRPurchase")
    public ResourcePurchase insertRPurchase(ResourcePurchase rpurchase) {
        // TODO
        NamespaceManager.set(rpurchase.getAccount());
        Key k = KeyFactory.createKey("ResourcePurchase", rpurchase.getpId());
        rpurchase.setKey(k);
        rpurchase.setKeyrep(KeyFactory.keyToString(k));
        EntityManager mgr = getEntityManager();
        System.out.println("---------HERE");
        try {
            if (containsRPurchase(rpurchase)) {
                throw new EntityExistsException("Object already exists");
            }
            else{
                rpurchase.setKeyrep(KeyFactory.keyToString(k));
                rpurchase.setAccount(KeyFactory.keyToString(k));
                mgr.getTransaction().begin();
                mgr.persist(rpurchase);
                mgr.getTransaction().commit();
            }
        } finally {
//            mgr.close();
        }
        return rpurchase;
    }

    /**
     * This method is used for updating an existing entity. If the entity does
     * not exist in the datastore, an exception is thrown. It uses HTTP PUT
     * method.
     *
     * @param-rpurchase
     *            the entity to be updated.
     * @return The updated entity.
     */
    @ApiMethod(name = "updateRPurchase")
    public ResourcePurchase updateRPurchase(ResourcePurchase rPurchase) {
        NamespaceManager.set(rPurchase.getAccount());
        Key k = KeyFactory.stringToKey(rPurchase.getKeyrep());
        rPurchase.setKey(k);
        ResourcePurchase currentRPurchase = getRPurchase(rPurchase.getAccount(),rPurchase.getKeyrep());
        EntityManager mgr = getEntityManager();
        try {
            if (!containsRPurchase(rPurchase)) {
                throw new EntityNotFoundException("Object does not exist");
            }
            else{
                if(rPurchase.getQtyRemaining()!=0)
                    currentRPurchase.setQtyRemaining(rPurchase.getQtyRemaining());
                if(rPurchase.getQuantifier()!=null)
                    currentRPurchase.setQuantifier(rPurchase.getQuantifier());
                if(rPurchase.getType()!=null)
                    currentRPurchase.setType(rPurchase.getType());
                mgr.getTransaction().begin();
                mgr.persist(currentRPurchase);
                mgr.getTransaction().commit();
            }
        } finally {
//            mgr.close();
        }
        return currentRPurchase;
    }

    /**
     * This method removes the entity with primary key id. It uses HTTP DELETE
     * method.
     *
     * //@param id
     *            the primary key of the entity to be deleted.
     */
    @ApiMethod(name = "removeRPurchase", httpMethod = HttpMethod.DELETE)
    public void removeRPurchase(@Named("keyrep") String keyrep,
                                @Named("namespace") String namespace) {
        System.out.println("1111111111111");
        NamespaceManager.set(namespace);
//        DatastoreService d = DatastoreServiceFactory.getDatastoreService();
      Key k = KeyFactory.stringToKey(keyrep);
//        try {
//            d.delete(k);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        EntityManager mgr = getEntityManager();
        ResourcePurchase rpFind = mgr.find(ResourcePurchase.class,k);
        if(rpFind!=null){
            try {
                mgr.getTransaction().begin();
                mgr.remove(rpFind);
                mgr.getTransaction().commit();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Resource Purchase Does Not Exist!");
        }
    }

//    @ApiMethod(name = "deletePurchase", httpMethod = HttpMethod.DELETE)
//    public void deletePurchase(@Named("keyrep") String keyrep,
//                               @Named("namespace") String namespace) {
//        System.out.println("1111111111111");
//        NamespaceManager.set(namespace);
//        Key k = KeyFactory.stringToKey(keyrep);
//        EntityManager mgr = getEntityManager();
//        ResourcePurchase purchase = mgr.find(ResourcePurchase.class, k);
//        try {
//            mgr.remove(purchase);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private boolean containsRPurchase(ResourcePurchase rpurchase) {
        NamespaceManager.set(rpurchase.getAccount());
        EntityManager mgr = getEntityManager();
        boolean contains = true;
        try {
            ResourcePurchase item = mgr.find(ResourcePurchase.class, rpurchase.getKey());
            if (item == null) {
                contains = false;
            }
        } finally {
//            mgr.close();
        }
        return contains;
    }

    private static EntityManager getEntityManager() {
        //return EMF.get().createEntityManager();
        return EMF.getManagerInstance();
    }
}
