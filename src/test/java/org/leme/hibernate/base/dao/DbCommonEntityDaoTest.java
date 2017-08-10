package org.leme.hibernate.base.dao;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.leme.hibernate.base.entity.DbEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 */
public class DbCommonEntityDaoTest {

    class TestDbEntity extends DbEntity<Integer> {

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

    private class TestDbEntityDao extends DbCommonEntityDao<TestDbEntity, Integer> {}

    private TestDbEntityDao dao;
    private SessionFactory sf;
    private Session s;
    private Query q;

    @Before
    public void setUp() throws Exception {
        sf = mock(SessionFactory.class);
        s = mock(Session.class);
        q = mock(Query.class);
        when(sf.openSession()).thenReturn(s);
        when(sf.getCurrentSession()).thenReturn(s);
        when(s.createQuery(any(String.class))).thenReturn(q);
        dao = new TestDbEntityDao();
        dao.setSessionFactory(sf);
    }

    @Test
    public void loadAll() throws Exception {
        List<TestDbEntity> l = new ArrayList<>();
        l.add(new TestDbEntity());
        when(q.list()).thenReturn(l);
        List<?> l2 = dao.loadAll();
        assertEquals(l,l2);
    }

    @Test
    public void getCurrentSession(){
        assertSame(s, dao.getCurrentSession());
    }

    @Test
    public void getSessionFactory() {
        assertSame(sf, dao.getSessionFactory());
    }

    @Test
    public void getCurrentSessionFactory(){
        assertSame(sf, dao.getCurrentSessionFactory());
    }

}