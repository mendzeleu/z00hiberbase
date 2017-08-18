package org.leme.hibernate.base.dao.jpa.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by mendzl on 8/17/2017.
 */
@Configuration
@ComponentScan(basePackages = {"org.leme.hibernate.base"})
@EnableTransactionManagement
public class TestJpaConfig {

    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory().createEntityManager();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "org.leme.hibernate.base" });
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );
        em.setJpaProperties(additionalProperties());
        em.afterPropertiesSet();
        return em.getObject();
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite::memory:");
        dataSource.setUsername( "" );
        dataSource.setPassword( "" );
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        //properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.dialect", "org.leme.z00.components.dao.SQLiteDialect");
        properties.put("hibernate.show_sql", "true");
    //    properties.put("hibernate.format_sql", "true");
    //    properties.put("hibernate.use_sql_comments", "true");
//        properties.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
//        properties.put("javax.persistence.jdbc.url", "jdbc:sqlite:target/z00_db_jpa.sqlite");
//        properties.put("javax.persistence.jdbc.user", ""); //if needed
        return properties;


    }
}
