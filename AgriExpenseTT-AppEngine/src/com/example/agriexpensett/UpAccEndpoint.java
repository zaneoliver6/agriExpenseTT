package com.example.agriexpensett;

import com.example.agriexpensett.EMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;


@Api(name = "upaccendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath="agriexpensett"))
public class UpAccEndpoint {

  /**
   * This method lists all the entities inserted in datastore.
   * It uses HTTP GET method and paging support.
   *
   * @return A CollectionResponse class containing the list of all entities
   * persisted and a cursor to the next page.
   */
  @SuppressWarnings({"unchecked", "unused"})
  @ApiMethod(name = "listUpAcc")
  public CollectionResponse<UpAcc> listUpAcc(
    @Nullable @Named("cursor") String cursorString,
    @Nullable @Named("limit") Integer limit) {

    EntityManager mgr = null;
    Cursor cursor = null;
    List<UpAcc> execute = null;

    try{
      mgr = getEntityManager();
      Query query = mgr.createQuery("select from UpAcc as UpAcc");
      if (cursorString != null && cursorString != "") {
        cursor = Cursor.fromWebSafeString(cursorString);
        query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
      }

      if (limit != null) {
        query.setFirstResult(0);
        query.setMaxResults(limit);
      }

      execute = (List<UpAcc>) query.getResultList();
      cursor = JPACursorHelper.getCursor(execute);
      if (cursor != null) cursorString = cursor.toWebSafeString();

      // Tight loop for fetching all entities from datastore and accomodate
      // for lazy fetch.
      for (UpAcc obj : execute);
    } finally {
      mgr.close();
    }

    return CollectionResponse.<UpAcc>builder()
      .setItems(execute)
      .setNextPageToken(cursorString)
      .build();
  }

  /**
   * This method gets the entity having primary key id. It uses HTTP GET method.
   *
   * @param id the primary key of the java bean.
   * @return The entity with primary key id.
   */
  @ApiMethod(name = "getUpAcc")
  public UpAcc getUpAcc(@Named("id") Long id, @Named("namespace") String namespace) {
	NamespaceManager.set(namespace);
    EntityManager mgr = getEntityManager();
    UpAcc upacc  = null;
    try {
      upacc = mgr.find(UpAcc.class, id);
      System.out.println("CLOUD-ACCOUNT:"+upacc.toString());
    }catch(Exception e){
    	upacc=null;
    } finally {
      mgr.close();
    }
    return upacc;
  }

  /**
   * This inserts a new entity into App Engine datastore. If the entity already
   * exists in the datastore, an exception is thrown.
   * It uses HTTP POST method.
   *
   * @param upacc the entity to be inserted.
   * @return The inserted entity.
   */
  @ApiMethod(name = "insertUpAcc")
  public UpAcc insertUpAcc(UpAcc upacc) {
	NamespaceManager.set(upacc.getAcc());
    EntityManager mgr = getEntityManager();
    Key k=KeyFactory.createKey("UpAcc",1);
    upacc.setKey(k);
    upacc.setKeyrep(KeyFactory.keyToString(k));
    try {
      if(containsUpAcc(upacc)) {
        throw new EntityExistsException("Object already exists");
      }
      mgr.persist(upacc);
    } finally {
      mgr.close();
    }
    return upacc;
  }

  /**
   * This method is used for updating an existing entity. If the entity does not
   * exist in the datastore, an exception is thrown.
   * It uses HTTP PUT method.
   *
   * @param upacc the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod(name = "updateUpAcc")
  public void updateUpAcc(UpAcc upacc) {
	NamespaceManager.set(upacc.getAcc());
    EntityManager mgr = getEntityManager();
    Key k=KeyFactory.stringToKey(upacc.getKeyrep());
    UpAcc acc=mgr.find(UpAcc.class,k);
    if(acc.getLastUpdated()<upacc.getLastUpdated()){
    	acc.setLastUpdated(upacc.getLastUpdated());
    	mgr.persist(acc);
    }
    mgr.close();
    
    return;
  }

  /**
   * This method removes the entity with primary key id.
   * It uses HTTP DELETE method.
   *
   * @param id the primary key of the entity to be deleted.
   */
  @ApiMethod(name = "removeUpAcc")
  public void removeUpAcc(@Named("id") Long id) {
    EntityManager mgr = getEntityManager();
    try {
      UpAcc upacc = mgr.find(UpAcc.class, id);
      mgr.remove(upacc);
    } finally {
      mgr.close();
    }
  }

  private boolean containsUpAcc(UpAcc upacc) {
    EntityManager mgr = getEntityManager();
    boolean contains = true;
    try {
      UpAcc item = mgr.find(UpAcc.class, upacc.getKey());
      if(item == null) {
        contains = false;
      }else{
    	  System.out.println(item.toString());
      }
    } finally {
      mgr.close();
    }
    return contains;
  }

  private static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }

}