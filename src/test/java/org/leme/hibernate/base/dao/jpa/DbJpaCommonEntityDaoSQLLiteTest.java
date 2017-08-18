package org.leme.hibernate.base.dao.jpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leme.hibernate.base.dao.TestDbEntity;
import org.leme.hibernate.base.dao.jpa.config.TestJpaConfig;
import org.leme.hibernate.base.dao.JpaEntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@Transactional
public class DbJpaCommonEntityDaoSQLLiteTest {

    protected static final String TBL_TESTDBENTITY = "TESTDBENTITY";

    @Autowired
    @Qualifier("testDbEntityJpaDao")
    private JpaEntityDao<TestDbEntity, Integer> dao;

    public void initializeDb() {
        EntityManager em = dao.getEntityManager();
        em.joinTransaction();
        em.createNativeQuery("DROP TABLE IF EXISTS " + TBL_TESTDBENTITY).executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS " + TBL_TESTDBENTITY + "(" +
                "ID INTEGER Primary key, " +
                "NAME varchar(50) not null) ").executeUpdate();

        em.createNativeQuery("INSERT INTO " + TBL_TESTDBENTITY + "(ID, NAME) VALUES(1,'Ann') ").executeUpdate();
        em.createNativeQuery("INSERT INTO " + TBL_TESTDBENTITY + "(ID, NAME) VALUES(2,'Bob') ").executeUpdate();
        em.createNativeQuery("INSERT INTO " + TBL_TESTDBENTITY + "(ID, NAME) VALUES(3,'Leo') ").executeUpdate();
    }

    @Before
    public void setUp() throws Exception {
        initializeDb();
    }

    @After
    public void tearDown() throws Exception {
        EntityManager em = dao.getEntityManager();
        em.createNativeQuery("DROP TABLE IF EXISTS TESTDBENTITY").executeUpdate();
    }

    @Test
    public void loadAll() throws Exception {
        List<TestDbEntity> list = dao.loadAll();
        assertEquals("Ann", list.get(0).getName());
        assertEquals("Bob", list.get(1).getName());
        assertEquals("Leo", list.get(2).getName());
    }

    @Test
    public void loadById() throws Exception {
        TestDbEntity e = dao.loadById(1);
        assertEquals("Ann", e.getName());
    }

    @Test
    public void findByNameTopOne() throws Exception {
        TestDbEntity e = dao.findByNameTopOne("Bob");
        assertEquals("Bob", e.getName());
    }

    @Test
    public void findByNameAll() throws Exception {
        List<TestDbEntity> list = dao.findByNameAll("Bob");
        assertEquals("Bob", list.get(0).getName());
    }

    @Test
    public void findWithPagination() throws Exception {
        List<TestDbEntity> list = dao.findWithPagination(10,0, "o");
        assertEquals(2, list.size());
        assertEquals("Bob", list.get(0).getName());
        list = dao.findWithPagination(1,0, "o");
        assertEquals(1, list.size());
        assertEquals("Bob", list.get(0).getName());
    }


    @Test
    public void save() throws Exception {
        TestDbEntity e = new TestDbEntity();
        e.setName("Tom");
        dao.save(e);
        List<TestDbEntity> list = dao.findByNameAll("Tom");
        assertEquals("Tom", list.get(0).getName());
    }

    @Test
    public void saveCollection() throws Exception {
        TestDbEntity tom = new TestDbEntity();
        tom.setName("Tom");
        TestDbEntity bob = dao.findByNameTopOne("Bob");
        bob.setName("Bob2");
        List<TestDbEntity> lst = new ArrayList<>();
        lst.add(bob);
        lst.add(tom);
        //save
        dao.save(lst);
        //validate
        List<TestDbEntity> listTom = dao.findByNameAll("Tom");
        List<TestDbEntity> listBob = dao.findByNameAll("Bob");
        List<TestDbEntity> listBob2 = dao.findByNameAll("Bob2");
        assertEquals("Tom", listTom.get(0).getName());
        assertEquals(0, listBob.size());
        assertEquals("Bob2", listBob2.get(0).getName());
    }

    @Test
    public void delete() throws Exception {
        TestDbEntity e = dao.findByNameTopOne("Bob");
        dao.delete(e);
        List<TestDbEntity> list = dao.loadAll();
        assertEquals("Ann", list.get(0).getName());
        assertEquals("Leo", list.get(1).getName());
    }


    @Test
    public void deleteCollection() throws Exception {
        TestDbEntity bob = dao.findByNameTopOne("Bob");
        TestDbEntity ann = dao.findByNameTopOne("Ann");
        List<TestDbEntity> lst = new ArrayList<>();
        lst.add(bob);
        lst.add(ann);
        dao.delete(lst);
        List<TestDbEntity> list = dao.loadAll();
        assertEquals(1, list.size());
        assertEquals("Leo", list.get(0).getName());
    }


    @Test
    public void dropTable() throws Exception {
        dao.dropTable(TBL_TESTDBENTITY);
        boolean success = false;
        try {
            dao.loadAll();
        } catch (Exception e) {
            Throwable cause = e.getCause().getCause();
            if ((cause instanceof SQLException) && (cause.getMessage().contains("no such table:"))) {
                success = true;
            }
        }
        assertTrue(success);
    }

}