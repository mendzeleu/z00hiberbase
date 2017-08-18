package org.leme.hibernate.base.dao.hibernate.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.leme.hibernate.base.dao.TestDbEntity;
import org.leme.hibernate.base.entity.DbEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leanid Mendzeleu on 9/2/2016.
 */
public class SQLLiteDbConnectionFactory {

    private static Map<String, SQLLiteDbConnectionFactory> factories = new HashMap<>();
    private SessionFactory sessionFactory;

    private SQLLiteDbConnectionFactory(String dbUrl){
        //factory = this;

        Configuration cfg = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                .setProperty("hibernate.dialect", "org.leme.z00.components.dao.SQLiteDialect")
                .setProperty("hibernate.connection.url",dbUrl)
        //"jdbc:sqlite:target/z00_db.sqlite
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
                .addPackage("org.leme.z00.components.entity")
                .addAnnotatedClass(TestDbEntity.class)
                .addAnnotatedClass(DbEntity.class);

        sessionFactory = cfg.buildSessionFactory();
    }

    public static SQLLiteDbConnectionFactory getInstance(String dbUrl){
        if(factories.containsKey(dbUrl)){
            return factories.get(dbUrl);
        }else{
            SQLLiteDbConnectionFactory factory = new SQLLiteDbConnectionFactory(dbUrl);
            factories.put(dbUrl, factory);
            return factory;
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
    }
}
