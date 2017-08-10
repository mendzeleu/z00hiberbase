package org.leme.hibernate.base.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.leme.hibernate.base.entity.DbEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 * Prent class for hibernate JPA DAO's. Providing basic operations with entities
 */
public abstract class DbJpaCommonEntityDao<E extends DbEntity, S extends Serializable> implements EntityDao<E, S> {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    //
    private static Logger logger = LoggerFactory.getLogger(DbJpaCommonEntityDao.class.getSimpleName());
    private static final String LOG_MARKER_TXT = "[HIBERNATE-JPA-SESSIONS] ";
    protected static final Marker logMarker = new BasicMarkerFactory().getMarker(LOG_MARKER_TXT);
    //
    private Class clazz;
    private String clazzName;


    public DbJpaCommonEntityDao() {
        Class<?> clazzDao = this.getClass();
        clazz = (Class)(((ParameterizedType) clazzDao.getGenericSuperclass()).getActualTypeArguments()[0]);
        clazzName = (((ParameterizedType) clazzDao.getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @SuppressWarnings("unchecked")
    public List<E> loadAll() {
        logger.debug(logMarker,"[LOADALL] Class: {} Session: {}", clazzName, entityManager);
        String hql = "FROM " + clazzName;
        Query query = entityManager.createQuery(hql);
        return (List<E>)query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public E loadById(Integer id) {
        logger.debug(logMarker,"[LOADBYID] Class: {} Id: {}  Session: {}", clazzName, id, entityManager);
        return (E) entityManager.getReference(clazz, id);
    }

    @SuppressWarnings("unchecked")
    public E findByNameTopOne(String name)
    {
        logger.debug(logMarker,"[FINDBYNAMETOPONE] Class: {} Name: {}  Session: {}", clazzName, name, entityManager);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = cb.createQuery(clazz);
        Root<E> obj = criteriaQuery.from(clazz);
        criteriaQuery.where(cb.equal(obj.get("name"), name));
        List<E> list = entityManager.createQuery(criteriaQuery).getResultList();
        return (list != null && list.size() > 0 && list.get(0) != null) ? list.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<E> findByNameAll(String name)
    {
        logger.debug(logMarker,"[FINDBYNAME] Class: {} Name: {}  Session: {}", clazzName, name, entityManager);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = cb.createQuery(clazz);
        Root<E> obj = criteriaQuery.from(clazz);
        criteriaQuery.where(cb.equal(obj.get("name"), name));
        List<E> list = entityManager.createQuery(criteriaQuery).getResultList();
        return list;
    }

    @Transactional(readOnly = false)
    public E save(E entity)
    {
        log("[SAVE]", entity, entityManager);
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Transactional(readOnly = false)
    public void save(Collection<E> entities) {
        logger.debug(logMarker,"[SAVE COLLECTION] Class: {}, EntityManager:{}", clazzName, entityManager);
        for(E entity : entities) {
            logger.trace(logMarker,"[SAVE COLLECTION] Class: {}, Entity: {}, EntityManager:{}", clazzName, entity, entityManager);
            entityManager.persist(entity);
        }
    }

    @Transactional(readOnly = false)
    public E delete(E entity)
    {
        log("[DELETE]", entity, entityManager);
        entityManager.remove(entity);
        entityManager.flush();
        return entity;
    }

    @Transactional(readOnly = false)
    public void delete(Collection<E> entities) {
        logger.debug(logMarker,"[DELETE COLLECTION] Class: {}, EntityManager:{}", clazzName, entityManager);
        for(E entity : entities) {
            logger.trace(logMarker,"[DELETE COLLECTION] Class: {}, Entity: {}, EntityManager:{}", clazzName, entity, entityManager);
            entityManager.remove(entity);
        }
    }

    @Transactional(readOnly = false)
    public void dropTable(String tableName) {
        logger.debug(logMarker,"[DROP TABLE] Table: {}", tableName);
        entityManager.createNativeQuery(String.format("DROP TABLE IF EXISTS `%s`;", tableName)).executeUpdate();
        logger.debug(logMarker,"[DROP TABLE DONE] Table: {}", tableName);
    }

    private void log(String msg, E entity, EntityManager s){
        int hash = entity != null ? entity.hashCode() : -1;
        String className = entity != null ? entity.getClass().getSimpleName() : "null";
        int sessionHash = s != null ? s.hashCode() : -1;
        logger.debug(logMarker, msg + ". Session Hash: {}, Entity Hash: {}, Entity Class: {}", sessionHash, hash, className);
    }

}
