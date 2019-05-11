package tech.graphits.catalogue.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories("tech.graphits.catalog.repository")
public class Neo4jConfiguration {

  @Bean
  public SessionFactory sessionFactory() {
    return new SessionFactory(configuration(), "tech.graphits.catalog.model");
  }

  @Bean
  public org.neo4j.ogm.config.Configuration configuration() {
    return new org.neo4j.ogm.config.Configuration.Builder() // <1>
        .uri("bolt://localhost:7687")
        .credentials("neo4j", "neo4j").build();
  }

  @Bean
  public Neo4jTransactionManager transactionManager() {
    return new Neo4jTransactionManager(sessionFactory());
  }
}
