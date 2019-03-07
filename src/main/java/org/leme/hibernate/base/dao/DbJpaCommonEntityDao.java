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
import org.springframework.stereotype.Component;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 * Parent class for hibernate JPA DAO's. Providing basic operations with entities
 */
@Component
public abstract class DbJpaCommonEntityDao<E extends DbEntity, S extends Serializable> extends AbstractEntityDao<E> implements JpaEntityDao<E, S> {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    //
    private static final String LOG_MARKER_TXT = "[HIBERNATE-JPA-SESSIONS] ";
    //
    private Class clazz;
    private String clazzName;


    public DbJpaCommonEntityDao() {
        Class<?> clazzDao = this.getClass();
        clazz = (Class)(((ParameterizedType) clazzDao.getGenericSuperclass()).getActualTypeArguments()[0]);
        clazzName = (((ParameterizedType) clazzDao.getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName();
    }

    @Override
    public Marker getLogMarker() {
        return new BasicMarkerFactory().getMarker(LOG_MARKER_TXT);
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass().getSimpleName());
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @SuppressWarnings("unchecked")
    public List<E> loadAll() {
        log("[LOADALL]", clazzName, entityManager);
        String hql = "FROM " + clazzName;
        Query query = entityManager.createQuery(hql);
        return (List<E>)query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public E loadById(Integer id) {
        log("[LOADBYID]", clazzName, id, entityManager);
        return (E) entityManager.getReference(clazz, id);
    }

    @SuppressWarnings("unchecked")
    public E findByNameTopOne(String name)
    {
        log("[FINDBYNAMETOPONE]", clazzName, name, entityManager);
        List<E> list = findByNameAll(name);
        return (list != null && list.size() > 0 && list.get(0) != null) ? list.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<E> findByNameAll(String name)
    {
        log("[FINDBYNAME]", clazzName, name, entityManager);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = cb.createQuery(clazz);
        Root<E> obj = criteriaQuery.from(clazz);
        criteriaQuery.where(cb.equal(obj.get("name"), name));
        List<E> list = entityManager.createQuery(criteriaQuery).getResultList();
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<E> findWithPagination(Integer size, Integer page, String namePattern)
    {
        log("[FINDWITHPAGINATION]", clazzName, namePattern, entityManager);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = cb.createQuery(clazz);
        Root<E> obj = criteriaQuery.from(clazz);
        criteriaQuery.where(cb.like(obj.get("name"), "%"+namePattern+"%"));
        Query query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(page*size).setMaxResults(size);
        List<E> list = query.getResultList();
        return (list != null) ? list : new ArrayList<E>();
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
        log("[SAVE]", clazzName, entityManager);
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
        log("[DELETE COLLECTION]", clazzName, entityManager);
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


}
