package com.rdlab.universityregistrar.configuration.test;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"com.rdlab.universityregistrar.model.dao.implementation"})
@PropertySource("classpath:test/db/hibernateTest.properties")
@EnableTransactionManagement
public class HibernateDaoTestContextConfiguration implements EnvironmentAware {
    private Environment env;

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer getPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource getTestDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    private Properties getHibernateProperties() {
        Properties props = new Properties();

        props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        props.setProperty("javax.persistence.schema-generation.database.action", env.getProperty("javax.persistence.schema-generation.database.action"));
        props.setProperty("javax.persistence.schema-generation.create-source", env.getProperty("javax.persistence.schema-generation.create-source"));
        props.setProperty("javax.persistence.schema-generation.create-script-source", env.getProperty("javax.persistence.schema-generation.create-script-source"));
        props.setProperty("javax.persistence.schema-generation.drop-source", env.getProperty("javax.persistence.schema-generation.drop-source"));
        props.setProperty("javax.persistence.schema-generation.drop-script-source", env.getProperty("javax.persistence.schema-generation.drop-script-source"));

        return props;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setDataSource(getTestDataSource());
        sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
        sessionFactory.setHibernateProperties(getHibernateProperties());

        return sessionFactory;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }
}
