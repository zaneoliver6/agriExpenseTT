package com.example.agriexpensett;

import com.example.agriexpensett.EMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "cycleendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath="agriexpensett"))
public class CycleEndpoint {

  /**
   * This method lists all the entities inserted in datastore.
   * It uses HTTP GET method and paging support.
   *
   * @return A CollectionResponse class containing the list of all entities
   * persisted and a cursor to the next page.
   */
  @SuppressWarnings({"unchecked", "unused"})
  @ApiMethod(name = "listCycle")
  public CollectionResponse<Cycle> listCycle(
    @Nullable @Named("cursor") String cursorString,
    @Nullable @Named("limit") Integer limit) {

    EntityManager mgr = null;
    Cursor cursor = null;
    List<Cycle> execute = null;

    try{
      mgr = getEntityManager();
      Query query = mgr.createQuery("select from Cycle as Cycle");
      if (cursorString != null && cursorString != "") {
        cursor = Cursor.fromWebSafeString(cursorString);
        query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
      }

      if (limit != null) {
        query.setFirstResult(0);
        query.setMaxResults(limit);
      }

      execute = (List<Cycle>) query.getResultList();
      cursor = JPACursorHelper.getCursor(execute);
      if (cursor != null) cursorString = cursor.toWebSafeString();

      // Tight loop for fetching all entities from datastore and accomodate
      // for lazy fetch.
      for (Cycle obj : execute);
    } finally {
      mgr.close();
    }

    return CollectionResponse.<Cycle>builder()
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
  @ApiMethod(name = "getCycle")
  public Cycle getCycle(@Named("id") String id) {
    //EntityManager mgr = getEntityManager();
	  DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
	  Key k=KeyFactory.stringToKey(id);
	  Entity et = null;
	  try {
		et=datastore.get(k);
	} catch (com.google.appengine.api.datastore.EntityNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  if(et==null)
		  return null;
	  Cycle cycle=new Cycle();
	  cycle.setCropId((Integer) et.getProperty("cropId"));
	  cycle.setLandQty((Double) et.getProperty("landQty"));
	  cycle.setLandType((String) et.getProperty("landType"));
	  /*
	  Cycle cycle  = null;
    try {
      cycle = mgr.find(Cycle.class, id);
    } finally {
      mgr.close();
    }*/
    return cycle;
  }

  /**
   * This inserts a new entity into App Engine datastore. If the entity already
   * exists in the datastore, an exception is thrown.
   * It uses HTTP POST method.
   *
   * @param cycle the entity to be inserted.
   * @return The inserted entity.
   */
  @ApiMethod(name = "insertCycle")
  public Cycle insertCycle(Cycle cycle) {
	  Key k=KeyFactory.createKey("Cycle",cycle.getId());
	  cycle.setKey(k);
	  cycle.setKeyrep(KeyFactory.keyToString(k));
    EntityManager mgr = getEntityManager();
    try {
      if(containsCycle(cycle)) {
        throw new EntityExistsException("Object already exists");
      }
      mgr.persist(cycle);
    }catch(Exception e){
    	return null;
    } finally {
      mgr.close();
    }
    cycle.setAccount(KeyFactory.keyToString(k));//using account to store the string rep of the key
    return cycle;
	
  }

  /**
   * This method is used for updating an existing entity. If the entity does not
   * exist in the datastore, an exception is thrown.
   * It uses HTTP PUT method.
   *
   * @param cycle the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod(name = "updateCycle")
  public Cycle updateCycle(Cycle cycle) {
    EntityManager mgr = getEntityManager();
    try {
      if(!containsCycle(cycle)) {
        throw new EntityNotFoundException("Object does not exist");
      }
      mgr.persist(cycle);
    } finally {
      mgr.close();
    }
    return cycle;
  }

  /**
   * This method removes the entity with primary key id.
   * It uses HTTP DELETE method.
   *
   * @param id the primary key of the entity to be deleted.
   */
  @ApiMethod(name = "removeCycle")
  public void removeCycle(@Named("id") String id) {
	 
	Key k=KeyFactory.stringToKey(id);
	DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
	try{
		datastore.delete(k);
	}catch(Exception e){
		return;
	}
	  
	  /*
    EntityManager mgr = getEntityManager();
    //int i=Integer.getInteger(id);
    try {
      Cycle cycle = mgr.find(Cycle.class, id);
      mgr.remove(cycle);
    } finally {
      mgr.close();
    }*/
  }

  private boolean containsCycle(Cycle cycle) {
    EntityManager mgr = getEntityManager();
    boolean contains = true;
    try {
      Cycle item = mgr.find(Cycle.class, cycle.getKey());
      if(item == null) {
        contains = false;
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
