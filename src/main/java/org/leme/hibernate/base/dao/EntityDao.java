package org.leme.hibernate.base.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.leme.hibernate.base.entity.DbEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 * Prent interface for hibernate DAO's. Providing basic operations with entities
 */
public interface EntityDao<E extends DbEntity, S extends Serializable> {

    List<E> loadAll();
    E loadById(Integer id) throws ClassNotFoundException;
    E findByNameTopOne(String name);
    List<E> findByNameAll(String name);
    E save(E entity);
    void save(Collection<E> entities);
    E delete(E entity);
    void delete(Collection<E> entities);
    void dropTable(String tableName);
}
