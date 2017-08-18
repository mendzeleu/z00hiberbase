package org.leme.hibernate.base.dao;

import org.leme.hibernate.base.entity.DbEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;

/**
 * Created by mendzl on 8/17/2017.
 */
public interface JpaEntityDao<E extends DbEntity, S extends Serializable> extends EntityDao<E, S > {

    EntityManager getEntityManager();
    EntityManagerFactory getEntityManagerFactory();
}
