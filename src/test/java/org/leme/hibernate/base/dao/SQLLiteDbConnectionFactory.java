package org.leme.hibernate.base.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.leme.hibernate.base.entity.DbEntity;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 */
public class SQLLiteDbConnectionFactory {

    private static SQLLiteDbConnectionFactory factory;
    private SessionFactory sessionFactory;

    private SQLLiteDbConnectionFactory(){
        factory = this;

        Configuration cfg = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                .setProperty("hibernate.dialect", "org.leme.z00.components.dao.SQLiteDialect")
                .setProperty("hibernate.connection.url","jdbc:sqlite:target/z00_db.sqlite")
                .setProperty("hibernate.connection.username","")
                .setProperty("hibernate.connection.password","")
                .setProperty("hibernate.connection.pool_size","1")
                .setProperty("connection.pool_size","1")
                .setProperty("hibernate.c3p0.min_size","1")
                .setProperty("hibernate.c3p0.max_size","1")
                .setProperty("hibernate.order_updates", "true")
                .setProperty("hibernate.show_sql","true")
                .setProperty("hibernate.current_session_context_class","thread")
                .addPackage("org.leme.z00.components.dao")
                .addPackage("org.leme.z00.components.domain")
                .addAnnotatedClass(org.leme.hibernate.base.dao.DbCommonEntityDaoSQLLiteTest.TestDbEntity.class)
                .addAnnotatedClass(DbEntity.class);

        sessionFactory = cfg.buildSessionFactory();
    }

    public static SQLLiteDbConnectionFactory getInstance(){
        return factory != null ? factory : new SQLLiteDbConnectionFactory();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
