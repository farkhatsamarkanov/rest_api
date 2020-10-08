package com.rdlab.universityregistrar.configuration.test;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Properties;

/**
 * Configuration class used in integration tests. Contains controller,
 * service, dao layer beans, mapper beans
 * and H2 in-memory database data source.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.rdlab.universityregistrar.controller",
        "com.rdlab.universityregistrar.service.implementation",
        "com.rdlab.universityregistrar.service.mapper"})
@PropertySource("classpath:test/db/hibernateTest.properties")
@EnableTransactionManagement
@Import(HibernateDaoTestContextConfiguration.class)
public class IntegrationTestContextConfiguration {
}
