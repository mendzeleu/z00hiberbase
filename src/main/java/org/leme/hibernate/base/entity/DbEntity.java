package org.leme.hibernate.base.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 * Prent class for hibernate Entitie's.
 */
@MappedSuperclass
public abstract class DbEntity<S extends Serializable> {


    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public S id;

    public S getId() {
        return id;
    }

    public void setId(S id) {
        this.id = id;
    }
}
