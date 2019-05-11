package tech.graphits.catalogue;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tech.graphits.catalog.model.Person;
import tech.graphits.catalog.repository.PersonRepository;
import tech.graphits.catalog.repository.SimplePersonRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = tech.graphits.catalogue.config.Neo4jConfiguration.class)
public class SDNTest {

	@Autowired
    SimplePersonRepository repository; // <1>

	@Test
	public void search_for_all_authors() {

		Iterable<Person> all = repository.findAll(); // <2>

		List<Person> authors = new ArrayList<>();
		all.forEach(authors::add);
		assertEquals(3, authors.size());
	}

	@Autowired
	PersonRepository personRepository;

	@Test
	public void others() {
		List<Person> auteurs = personRepository.findAllByBooksTitle("Neo4j - A Graph Project Story");
		assertEquals(3, auteurs.size());

		Person sylvain = personRepository.findByNameLike("Sylvain*");
		assertNotNull(sylvain);

		int bookCountPerAuthor = personRepository.numberOfBooksForAuthor("Nicolas");
		assertEquals(1, bookCountPerAuthor);
	}
}
