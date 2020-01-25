package swapper9.moviedatabase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@ComponentScan("swapper9.moviedatabase.domain")
@EnableJpaRepositories(basePackages = "swapper9.moviedatabase.repository")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class DataConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource ();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return executor;
    }


  /*@Bean
  @Autowired
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) throws NamingException {
    JpaTransactionManager jpaTransaction = new JpaTransactionManager();
    jpaTransaction.setEntityManagerFactory(emf);
    return jpaTransaction;
  }

  @Bean
  public EntityManagerFactory entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setDataSource(dataSource());
    emf.setPackagesToScan("io.github.azanx.shopping_list.domain");
    //emf.setPersistenceUnitName("spring-jpa-unit");
    emf.setJpaVendorAdapter(getHibernateAdapter());
    Properties jpaProperties = new Properties();
    jpaProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
    jpaProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
    jpaProperties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
    emf.setJpaProperties(jpaProperties);
    emf.afterPropertiesSet();
    return emf.getObject();
  }

  @Bean
  public JpaVendorAdapter getHibernateAdapter() {
    return new HibernateJpaVendorAdapter();
  }*/

}
