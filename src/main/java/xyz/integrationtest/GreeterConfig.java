/*
 * Copyright 2015 Sharmarke Aden.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.integrationtest;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static javax.persistence.Persistence.createEntityManagerFactory;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.cfg.Environment;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import static org.springframework.transaction.support.TransactionSynchronizationManager.getResource;

/**
 * Greeting Spring Java Config.
 *
 * @author saden
 */
@ComponentScan
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class GreeterConfig {

    /**
     * An in-memory H2 database data source.
     *
     * @return the data source
     */
    @Bean
    DataSource dataSourceProvider() {
        JdbcDataSource dataSource = new JdbcDataSource();

        //XXX: The content of the database is lost at the moment the last
        //connection is closed. If you want to keep your content you have to
        //pass in DB_CLOSE_DELAY to -1
        dataSource.setURL("jdbc:h2:mem:greeter;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        return dataSource;
    }

    /**
     * Provides an entity manager factory based on the data source.
     *
     * @param ds the data source
     * @return the entity manager factory
     */
    @Bean
    EntityManagerFactory entityManagerFactoryProvider(DataSource ds) {
        Map<String, Object> props = new HashMap<>();
        props.put(Environment.DATASOURCE, ds);
        props.put(Environment.PHYSICAL_NAMING_STRATEGY, new PhysicalNamingStrategyStandardImpl());
        props.put(Environment.IMPLICIT_NAMING_STRATEGY, new ImplicitNamingStrategyComponentPathImpl());

        return createEntityManagerFactory("example.junit.spring.integrationtest", props);
    }

    /**
     * Provides a proxy instance of the current transaction's entity manager
     * which is ready to inject via constructor injection and guarantees
     * immutability.
     *
     * @param emf the entity manager factory
     * @return a proxy of entity manager
     */
    @Bean
    @Scope(SCOPE_PROTOTYPE)
    EntityManager entityManagerProvider(EntityManagerFactory emf) {
        EntityManagerHolder holder = (EntityManagerHolder) getResource(emf);

        if (holder == null) {
            return emf.createEntityManager();
        }

        return holder.getEntityManager();
    }

    /**
     * Provides JPA based Spring transaction manager.
     *
     * @param emf the entity manager factory
     * @return jpa transaction manager
     */
    @Bean
    JpaTransactionManager jpaTransactionManagerProvider(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager(emf);

        return transactionManager;
    }

}
