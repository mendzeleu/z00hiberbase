package org.leme.hibernate.base.dao;

import org.leme.hibernate.base.entity.DbEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by mendzl on 8/17/2017.
 */
@Entity
@Table(name = "TESTDBENTITY")
public class TestDbEntity extends DbEntity<Integer> {

    @Column(name = "NAME")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void mergeFrom(DbEntity entity) {
        this.name = ((TestDbEntity)entity).name;
    }
}

