package tech.graphits.catalog.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import tech.graphits.catalog.model.Person;

public interface SimplePersonRepository
    extends Neo4jRepository<Person, Long> { // <1>
}
