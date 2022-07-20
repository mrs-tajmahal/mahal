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

import com.fitbur.testify.Cut;
import com.fitbur.testify.Module;
import xyz.integrationtest.entity.GreetingEntity;
import xyz.integrationtest.service.Greeter;
import com.fitbur.testify.integration.SpringIntegrationTest;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

@Module(GreeterConfig.class)
@RunWith(SpringIntegrationTest.class)
public class GreeterRealTest {

    @Cut
    Greeter cut;

    @Inject
    EntityManagerFactory entityManagerFactory;

    @Test
    public void callToGreetShouldReturnHello() {
        //Arrange
        String phrase = "Hello";

        //Act
        cut.save(phrase);

        //Assert
        //Assert
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createQuery("SELECT e FROM GreetingEntity e");
        assertThat(query).isNotNull();
        List<GreetingEntity> entities = query.getResultList();
        assertThat(entities).hasSize(1);

        GreetingEntity entity = entities.get(0);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getPhrase()).isEqualTo(phrase);
        em.close();
    }
}
