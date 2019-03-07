package org.leme.hibernate.base.dao;

import org.hibernate.Session;
import org.leme.hibernate.base.entity.DbEntity;
import org.slf4j.Logger;
import org.slf4j.Marker;

public abstract class AbstractEntityDao<E extends DbEntity> {

    Marker logMarker = getLogMarker();
    Logger logger = getLogger();

    public abstract Marker getLogMarker();

    public abstract Logger getLogger();

    public void log(String msg, E entity, Object s){
        int hash = entity != null ? entity.hashCode() : -1;
        String className = entity != null ? entity.getClass().getSimpleName() : "null";
        int sessionHash = s != null ? s.hashCode() : -1;
        logger.debug(logMarker, msg + ". Session Hash: {}, Entity Class: {}, Entity Hash: {}", sessionHash, className, hash);
    }

    public void log(String msg, String className, Object s){
        int sessionHash = s != null ? s.hashCode() : -1;
        logger.debug(logMarker, msg + ". Session Hash: {}, Entity Class: {}", sessionHash, className);
    }


    public void log(String msg, String className, Integer id, Object s){
        int sessionHash = s != null ? s.hashCode() : -1;
        logger.debug(logMarker, msg + ". Session Hash: {}, Entity Class: {}, Entity Id: {}", sessionHash, className, id);
    }


    public void log(String msg, String className, String name, Object s){
        int sessionHash = s != null ? s.hashCode() : -1;
        logger.debug(logMarker, msg + ". Session Hash: {}, Entity Class: {}, Entity Name: {}", sessionHash, className, name);
    }
}
