package com.rdlab.universityregistrar.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Properties;

/**
 * Spring application configuration. Contains rest controller, service implementation
 * DAO implementation and mapper beans, Flyway database version control tool bean,
 * bean validator and placeholder configurer beans
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@EnableSwagger2
@ComponentScan(basePackages = {"com.rdlab.universityregistrar.controller",
        "com.rdlab.universityregistrar.service.implementation",
        "com.rdlab.universityregistrar.model.dao.implementation",
        "com.rdlab.universityregistrar.service.mapper"})
@PropertySources({
        @PropertySource("classpath:/db/hikaricp.properties"),
        @PropertySource("classpath:/db/hibernate.properties")
})
public class AppConfiguration implements EnvironmentAware {
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
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.rdlab.universityregistrar.controller"))
                .paths(PathSelectors.any())
                .build()
                .tags(
                        new Tag("Academic ranks API", "Interface to manage academic ranks"),
                        new Tag("Courses API", "Interface to manage courses"),
                        new Tag("Lecturers API", "Interface to manage lecturers"),
                        new Tag("Students API", "Interface to manage students"),
                        new Tag("Semesters API", "Interface to manage semesters"),
                        new Tag("Schedule of classes API", "Interface to manage schedule of classes"),
                        new Tag("Users API", "Interface to manage users")
                )
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("University registrar API")
                .description("API to manage courses, students, semesters, academic ranks, users, lecturers and class schedule information")
                .version("1.0.0")
                .build();
    }

    @Bean
    public HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName(env.getProperty("poolName"));
        config.setDataSourceClassName(env.getProperty("dataSourceClassName"));
        config.setMaximumPoolSize(Integer.parseInt(env.getProperty("maximumPoolSize")));
        config.setIdleTimeout(Integer.parseInt(env.getProperty("idleTimeout")));
        config.addDataSourceProperty("serverName", env.getProperty("dataSource.serverName"));
        config.addDataSourceProperty("portNumber", env.getProperty("dataSource.portNumber"));
        config.addDataSourceProperty("databaseName", env.getProperty("dataSource.databaseName"));
        config.setUsername(env.getProperty("dataSource.user"));
        config.setPassword(env.getProperty("dataSource.password"));
        return config;
    }

    @Bean
    @DependsOn("getHikariConfig")
    public DataSource getDataSource() {
        return new HikariDataSource(getHikariConfig());
    }

    @Bean
    @DependsOn("getDataSource")
    public ClassicConfiguration getFlywayConfig() {
        ClassicConfiguration configuration = new ClassicConfiguration();
        configuration.setDataSource(getDataSource());
        configuration.setBaselineOnMigrate(true);
        return configuration;
    }

    @Bean(initMethod = "migrate")
    @DependsOn("getFlywayConfig")
    public Flyway getFlyway() {
        return new Flyway(getFlywayConfig());
    }

    private Properties getHibernateProperties() {
        Properties props = new Properties();

        props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));

        return props;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setDataSource(getDataSource());
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

    @Bean
    public Validator getBeanValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
