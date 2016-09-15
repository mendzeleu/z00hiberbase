package org.leme.hibernate.base.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.leme.hibernate.base.entity.DbEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 */
public class DbCommonEntityDaoSQLLiteTest {

    @Entity
    @Table(name = "TESTDBENTITY")
    public static class TestDbEntity extends DbEntity<Integer> {

        @Column(name = "NAME")
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private class TestDbEntityDao extends DbCommonEntityDao<DbCommonEntityDaoSQLLiteTest.TestDbEntity, Integer> {}
    private DbCommonEntityDaoSQLLiteTest.TestDbEntityDao dao;
    private static final String TBL_TESTDBENTITY = "TESTDBENTITY";

    public DbCommonEntityDaoSQLLiteTest() {
        SessionFactory sessionFactory = SQLLiteDbConnectionFactory.getInstance().getSessionFactory();
        dao = new DbCommonEntityDaoSQLLiteTest.TestDbEntityDao();
        dao.setSessionFactory(sessionFactory);
    }

    public void initializeDb(){
            Session session = SQLLiteDbConnectionFactory.getInstance().getCurrentSession();
            Transaction t = session.beginTransaction();
            Query q = session.createSQLQuery("DROP TABLE IF EXISTS " + TBL_TESTDBENTITY);
            q.executeUpdate();
            q = session.createSQLQuery("CREATE TABLE IF NOT EXISTS " + TBL_TESTDBENTITY + "(" +
                    "ID INTEGER Primary key, " +
                    "NAME varchar(50) not null) "
                    );
            q.executeUpdate();
            session.createSQLQuery("INSERT INTO " + TBL_TESTDBENTITY + "(ID, NAME) VALUES(1,'Ann') ").executeUpdate();
            session.createSQLQuery("INSERT INTO " + TBL_TESTDBENTITY + "(ID, NAME) VALUES(2,'Bob') ").executeUpdate();
            session.createSQLQuery("INSERT INTO " + TBL_TESTDBENTITY + "(ID, NAME) VALUES(3,'Leo') ").executeUpdate();
            t.commit();
    }

    @Before
    public void setUp() throws Exception {
        initializeDb();
    }

    @After
    public void tearDown() throws Exception {
        Session session = SQLLiteDbConnectionFactory.getInstance().getCurrentSession();
        Transaction t = session.beginTransaction();
        Query q = session.createSQLQuery("DROP TABLE IF EXISTS TESTDBENTITY");
        q.executeUpdate();
        t.commit();
    }

    @Test
    public void loadAll() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> list = dao.loadAll();
        t.commit();
        assertEquals("Ann",list.get(0).getName());
        assertEquals("Bob",list.get(1).getName());
        assertEquals("Leo",list.get(2).getName());
    }

    @Test
    public void loadById() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        DbCommonEntityDaoSQLLiteTest.TestDbEntity e = dao.loadById(1);
        t.commit();
        assertEquals("Ann",e.getName());
    }

    @Test
    public void findByNameTopOne() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        DbCommonEntityDaoSQLLiteTest.TestDbEntity e = dao.findByNameTopOne("Bob");
        t.commit();
        assertEquals("Bob",e.getName());
    }

    @Test
    public void findByNameAll() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> list = dao.findByNameAll("Bob");
        t.commit();
        assertEquals("Bob",list.get(0).getName());
    }

    @Test
    public void save() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        DbCommonEntityDaoSQLLiteTest.TestDbEntity e = new DbCommonEntityDaoSQLLiteTest.TestDbEntity();
        e.setName("Tom");
        dao.save(e);
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> list = dao.findByNameAll("Tom");
        t.commit();
        assertEquals("Tom",list.get(0).getName());
    }

    @Test
    public void saveCollection() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        DbCommonEntityDaoSQLLiteTest.TestDbEntity tom = new DbCommonEntityDaoSQLLiteTest.TestDbEntity();
        tom.setName("Tom");
        DbCommonEntityDaoSQLLiteTest.TestDbEntity bob = dao.findByNameTopOne("Bob");
        bob.setName("Bob2");
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> lst = new ArrayList<>();
        lst.add(bob);
        lst.add(tom);
        //save
        dao.save(lst);
        //validate
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> listTom = dao.findByNameAll("Tom");
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> listBob = dao.findByNameAll("Bob");
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> listBob2 = dao.findByNameAll("Bob2");
        t.commit();
        assertEquals("Tom",listTom.get(0).getName());
        assertEquals(0,listBob.size());
        assertEquals("Bob2",listBob2.get(0).getName());
    }

    @Test
    public void delete() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        DbCommonEntityDaoSQLLiteTest.TestDbEntity e = dao.findByNameTopOne("Bob");
        dao.delete(e);
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> list = dao.loadAll();
        t.commit();
        assertEquals("Ann",list.get(0).getName());
        assertEquals("Leo",list.get(1).getName());
    }


    @Test
    public void deleteCollection() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        DbCommonEntityDaoSQLLiteTest.TestDbEntity bob = dao.findByNameTopOne("Bob");
        DbCommonEntityDaoSQLLiteTest.TestDbEntity ann = dao.findByNameTopOne("Ann");
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> lst = new ArrayList<>();
        lst.add(bob);
        lst.add(ann);
        dao.delete(lst);
        List<DbCommonEntityDaoSQLLiteTest.TestDbEntity> list = dao.loadAll();
        t.commit();
        assertEquals(1,list.size());
        assertEquals("Leo",list.get(0).getName());
    }


    @Test
    public void dropTable() throws Exception {
        Session s = dao.getCurrentSession();
        Transaction t = s.beginTransaction();
        dao.dropTable(TBL_TESTDBENTITY);
        t.commit();
        boolean success = false;
        try{
            Session s2 = dao.getCurrentSession();
            Transaction ts = s2.beginTransaction();
            dao.loadAll();
            ts.commit();
        }catch(Exception e){
            Throwable cause = e.getCause();
            if((cause instanceof SQLException)&&(cause.getMessage().contains("no such table:"))){
                success = true;
            }
        }
        assertTrue(success);
    }

}