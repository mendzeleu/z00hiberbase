package org.leme.hibernate.base.dao;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.leme.hibernate.base.entity.DbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 * Prent class for hibernate DAO's. Providing basic operations with entities
 */
public abstract class DbCommonEntityDao<E extends DbEntity, S extends Serializable> implements EntityDao<E, S> {

    @Autowired
    private SessionFactory sessionFactory;
    //
    private static Logger logger = LoggerFactory.getLogger(DbCommonEntityDao.class.getSimpleName());
    private static final String LOG_MARKER_TXT = "[HIBERNATE-SESSIONS] ";
    protected static final Marker logMarker = new BasicMarkerFactory().getMarker(LOG_MARKER_TXT);
    //
    private Class clazz;
    private String clazzName;


    public DbCommonEntityDao() {
        Class<?> clazzDao = this.getClass();
        clazz = (Class)(((ParameterizedType) clazzDao.getGenericSuperclass()).getActualTypeArguments()[0]);
        clazzName = (((ParameterizedType) clazzDao.getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName();
    }

    protected SessionFactory getCurrentSessionFactory() {
        return sessionFactory;
    }

    public final Session getCurrentSession() {
        Session s;
        try {
            s = sessionFactory.getCurrentSession();
        }catch (Exception e){
            s = sessionFactory.openSession();
        }
        return s;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public List<E> loadAll() {
        Session session = getCurrentSession();
        logger.debug(logMarker,"[LOADALL] Class: {} Session: {}", clazzName, session);
        String hql = "FROM " + clazzName;
        Query query = session.createQuery(hql);
        return (List<E>)query.list();
    }

    @SuppressWarnings("unchecked")
    public E loadById(Integer id) {
        Session session = getCurrentSession();
        logger.debug(logMarker,"[LOADBYID] Class: {} Id: {}  Session: {}", clazzName, id, session);
        return (E) session.get(clazz, id);
    }

    @SuppressWarnings("unchecked")
    public E findByNameTopOne(String name)
    {
        Session session = getCurrentSession();
        logger.debug(logMarker,"[FINDBYNAMETOPONE] Class: {} Name: {}  Session: {}", clazzName, name, session);
        Criteria criteria = session.createCriteria(clazz);
        List<E> list = (List<E>) criteria.add(Restrictions.eq("name", name)).list();
        return (list != null && list.get(0) != null) ? list.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public List<E> findByNameAll(String name)
    {
        Session session = getCurrentSession();
        logger.debug(logMarker,"[FINDBYNAME] Class: {} Name: {}  Session: {}", clazzName, name, session);
        Criteria criteria = session.createCriteria(clazz);
        return (List<E>) criteria.add(Restrictions.eq("name", name)).list();
    }

    @Transactional(readOnly = false)
    public E save(E entity)
    {
        Session session = getCurrentSession();
        log("[SAVE]", entity, session);
        session.saveOrUpdate(entity);
        session.flush();
        return entity;
    }

    @Transactional(readOnly = false)
    public void save(Collection<E> entities) {
        Session session = getCurrentSession();
        logger.debug(logMarker,"[SAVE COLLECTION] Class: {}, Session:{}", clazzName, session);
        for(E entity : entities) {
            logger.trace(logMarker,"[SAVE COLLECTION] Class: {}, Entity: {}, Session:{}", clazzName, entity, session);
            session.saveOrUpdate(entity);
        }
    }

    @Transactional(readOnly = false)
    public E delete(E entity)
    {
        Session session = getCurrentSession();
        log("[DELETE]", entity, session);
        session.delete(entity);
        session.flush();
        return entity;
    }

    @Transactional(readOnly = false)
    public void delete(Collection<E> entities) {
        Session session = getCurrentSession();
        logger.debug(logMarker,"[DELETE COLLECTION] Class: {}, Session:{}", clazzName, session);
        for(E entity : entities) {
            logger.trace(logMarker,"[DELETE COLLECTION] Class: {}, Entity: {}, Session:{}", clazzName, entity, session);
            session.delete(entity);
        }
    }

    public void dropTable(String tableName) {
        logger.debug(logMarker,"[DROP TABLE] Table: {}", tableName);
        getCurrentSession().createSQLQuery(String.format("DROP TABLE IF EXISTS `%s`;", tableName)).executeUpdate();
        logger.debug(logMarker,"[DROP TABLE DONE] Table: {}", tableName);
    }

    private void log(String msg, E entity, Session s){
        int hash = entity != null ? entity.hashCode() : -1;
        String className = entity != null ? entity.getClass().getSimpleName() : "null";
        int sessionHash = s != null ? s.hashCode() : -1;
        logger.debug(logMarker, msg + ". Session Hash: {}, Entity Hash: {}, Entity Class: {}", sessionHash, hash, className);
    }


//    Second possible way of identifying generic class. Investigate in the future
//
//    private Class getType(){
//        Class< ?> cls = getClass();
//        while (!(cls.getSuperclass() == null
//                || cls.getSuperclass().equals(AbstractExpression.class))) {
//            cls = cls.getSuperclass();
//        }
//
//        if (cls.getSuperclass() == null)
//            throw new RuntimeException("Unexpected exception occurred.");
//
//        return ((Class) ((ParameterizedType)
//                cls.getGenericSuperclass()).getActualTypeArguments()[0]);
//    }


//    Third possible way of identifying generic class. Investigate in the future
//
//    public static Class<?> findSubClassParameterType(Object instance, Class<?> classOfInterest, int parameterIndex) {
//        Map<Type, Type> typeMap = new HashMap<Type, Type>();
//        Class<?> instanceClass = instance.getClass();
//        while (classOfInterest != instanceClass.getSuperclass()) {
//            extractTypeArguments(typeMap, instanceClass);
//            instanceClass = instanceClass.getSuperclass();
//            if (instanceClass == null) throw new IllegalArgumentException();
//        }
//
//        ParameterizedType parameterizedType = (ParameterizedType) instanceClass.getGenericSuperclass();
//        Type actualType = parameterizedType.getActualTypeArguments()[parameterIndex];
//        if (typeMap.containsKey(actualType)) {
//            actualType = typeMap.get(actualType);
//        }
//
//        if (actualType instanceof Class) {
//            return (Class<?>) actualType;
//        } else if (actualType instanceof TypeVariable) {
//            return browseNestedTypes(instance, (TypeVariable<?>) actualType);
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }
//    private static Class<?> browseNestedTypes(Object instance, TypeVariable<?> actualType) {
//        Class<?> instanceClass = instance.getClass();
//        List<Class<?>> nestedOuterTypes = new LinkedList<Class<?>>();
//        for (
//                Class<?> enclosingClass = instanceClass.getEnclosingClass();
//                enclosingClass != null;
//                enclosingClass = enclosingClass.getEnclosingClass()) {
//            try {
//                Field this$0 = instanceClass.getDeclaredField("this$0");
//                Object outerInstance = this$0.get(instance);
//                Class<?> outerClass = outerInstance.getClass();
//                nestedOuterTypes.add(outerClass);
//                Map<Type, Type> outerTypeMap = new HashMap<Type, Type>();
//                extractTypeArguments(outerTypeMap, outerClass);
//                for (Map.Entry<Type, Type> entry : outerTypeMap.entrySet()) {
//                    if (!(entry.getKey() instanceof TypeVariable)) {
//                        continue;
//                    }
//                    TypeVariable<?> foundType = (TypeVariable<?>) entry.getKey();
//                    if (foundType.getName().equals(actualType.getName())
//                            && isInnerClass(foundType.getGenericDeclaration(), actualType.getGenericDeclaration())) {
//                        if (entry.getValue() instanceof Class) {
//                            return (Class<?>) entry.getValue();
//                        }
//                        actualType = (TypeVariable<?>) entry.getValue();
//                    }
//                }
//            } catch (NoSuchFieldException e) { /* this should never happen */ } catch (IllegalAccessException e) { /* this might happen */}
//
//        }
//        throw new IllegalArgumentException();
//    }
//    private static boolean isInnerClass(GenericDeclaration outerDeclaration, GenericDeclaration innerDeclaration) {
//        if (!(outerDeclaration instanceof Class) || !(innerDeclaration instanceof Class)) {
//            throw new IllegalArgumentException();
//        }
//        Class<?> outerClass = (Class<?>) outerDeclaration;
//        Class<?> innerClass = (Class<?>) innerDeclaration;
//        while ((innerClass = innerClass.getEnclosingClass()) != null) {
//            if (innerClass == outerClass) {
//                return true;
//            }
//        }
//        return false;
//    }
//    private static void extractTypeArguments(Map<Type, Type> typeMap, Class<?> clazz) {
//        Type genericSuperclass = clazz.getGenericSuperclass();
//        if (!(genericSuperclass instanceof ParameterizedType)) {
//            return;
//        }
//
//        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
//        Type[] typeParameter = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
//        Type[] actualTypeArgument = parameterizedType.getActualTypeArguments();
//        for (int i = 0; i < typeParameter.length; i++) {
//            if(typeMap.containsKey(actualTypeArgument[i])) {
//                actualTypeArgument[i] = typeMap.get(actualTypeArgument[i]);
//            }
//            typeMap.put(typeParameter[i], actualTypeArgument[i]);
//        }
//    }

}
