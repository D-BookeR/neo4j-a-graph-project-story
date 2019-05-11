package tech.graphits.catalog.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import tech.graphits.catalog.model.Person;

import java.util.List;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

  Person findByEmail(String email); // <1>

  Person findByNameLike(String nom); // <2>

  List<Person> findAllByBooksTitle(String titre); // <3>

  @Query("MATCH (b:Book)<-[:IS_AUTHOR_OF]-(p:Person) WHERE p.name = {name} return count(b)")
  int numberOfBooksForAuthor(@Param("name") String name); // <4>
}
