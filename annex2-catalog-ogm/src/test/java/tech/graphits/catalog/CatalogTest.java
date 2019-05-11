package tech.graphits.catalog;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.ConfigurationSource;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.cypher.query.SortOrder;
import org.neo4j.ogm.driver.Driver;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;
import tech.graphits.catalog.model.HasCategory;
import tech.graphits.catalog.model.Book;
import tech.graphits.catalog.model.Person;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.neo4j.ogm.cypher.ComparisonOperator.EQUALS;

public class CatalogTest {

    private Session session;
    private SessionFactory sessionFactory;

    @Before
    public void setup() throws IOException {
        sessionFactory = new SessionFactory("tech.graphits.catalog.model");
        session = sessionFactory.openSession();

        session.purgeDatabase();
        session.query(Resources.toString(Resources.getResource("test_data.cql"), Charsets.UTF_8), Collections.emptyMap());
    }

    @After
    public void rollback() {
        session.clear();
    }

    public void init_through_properties() {

        ConfigurationSource props = new ClasspathConfigurationSource("ogm.properties");
        Configuration config = new Configuration.Builder(props).build();
        sessionFactory = new SessionFactory(config, "tech.graphits.catalog.model");
    }

    public void init_through_java_code() {

        Configuration config = new Configuration.Builder()
            .uri("bolt://myServer")
            .credentials("user", "password")
            .build();
        sessionFactory = new SessionFactory(config, "tech.graphits.catalog.model");
    }

    public void init_with_native_driver() {

        org.neo4j.driver.v1.Driver nativeDriver = GraphDatabase.driver("bolt://myServer");
        Driver ogmDriver = new BoltDriver(nativeDriver);
        sessionFactory = new SessionFactory(ogmDriver, "tech.graphits.catalog.model");
    }

    @Test
    public void search_with_queryForObject() {

        Person nicolas = session.queryForObject(Person.class, // <2>
                "match (p:Person{email:{email}}) return p", // <3>
                Collections.singletonMap("email", "nicolas@graphits.tech")); // <4>

        assertEquals("Nicolas", nicolas.getName());
        assertEquals("nicolas@graphits.tech", nicolas.getEmail());

        // check related entities are not loaded
        assertEquals(0, nicolas.getBooks().size());
    }

    @Test
    public void search_with_loadAll() {

        Filter emailFilter = new Filter("email", EQUALS, "nicolas@graphits.tech");
        Collection<Person> results = session.loadAll(
                Person.class // <1>
                , new Filters(emailFilter) // <2>
                , 1); // <3>

        Person result = results.iterator().next();
        assertEquals("Nicolas", result.getName());
        assertEquals("nicolas@graphits.tech", result.getEmail());

        // load first level related entities
        assertEquals(1 , result.getBooks().size());
        Book book = result.getBooks().iterator().next();
        assertEquals("Neo4j - A Graph Project Story", book.getTitle());

        // check we don't load up to depth 2
        assertNull(book.getProducts());
    }

    @Test
    public void search_with_query() {

        Result results = session.query("MATCH (p:Person) --> (b:Book) RETURN p.name as name, count(b) as booksCount", Collections.emptyMap());

        Map<Object, Object> allResults = new HashMap<>();
        results.forEach(map -> allResults.put(map.get("name"), map.get("booksCount")));
        assertEquals(3, allResults.size());
        assertThat(allResults.keySet(), hasItems("Nicolas", "Frank", "Sylvain"));
    }

    @Test
    public void simple_data_update() {

        long personCount = session.countEntitiesOfType(Person.class);

        Person person = new Person();
        person.setName("Brian");
        person.setEmail("brian@graphit.tech");

        session.save(person);

        assertEquals(personCount + 1, session.countEntitiesOfType(Person.class));
    }

    @Test
    public void cascade_save() {

        session.purgeDatabase();

        Book book = new Book();
        book.setTitle("a title");
        book.setMainTitle("a main title");
        book.setSubTitle("a sub title");

        Person author = new Person();
        author.setEmail("brian@graphits.tech");
        author.setName("Brian");
        author.setBooks(Collections.singleton(book));

        session.save(author);

        assertEquals(1, session.countEntitiesOfType(Person.class));
        assertEquals(1, session.countEntitiesOfType(Book.class));

        Filter titleFilter = new Filter("title", EQUALS, "a title");
        Book newBook = session.loadAll(Book.class, new Filters(titleFilter)).iterator().next();
        assertEquals("a title", newBook.getTitle());

        book.setSubTitle("a new sub title");

        session.save(author);

        newBook = session.loadAll(Book.class, new Filters(titleFilter)).iterator().next();
        assertEquals("a new sub title", newBook.getSubTitle());
    }

    @Test
    public void cascade_update() {

        Filter emailFilter = new Filter("email", EQUALS, "nicolas@graphits.tech");
        Person nicolas = session.loadAll(Person.class, new Filters(emailFilter)).iterator().next();

        // let's change the email of the author
        final String newEmail = "nicolas@graphits.tech";
        nicolas.setEmail(newEmail);
        // and also change related book title
        nicolas.getBooks().iterator().next().setSubTitle("a test");
        // add add a book
        Book tome1 = new Book("Neo4j - Discover", "Neo4j", "Discover Neo4j");
        nicolas.getBooks().add(tome1);

        session.save(nicolas);

        nicolas = session.load(Person.class, nicolas.getId());
        assertEquals(newEmail, nicolas.getEmail());
        assertEquals(2, nicolas.getBooks().size());

        Set<String> titres = nicolas.getBooks().stream().map(Book::getTitle).collect(Collectors.toSet());
        assertTrue(titres.contains("Neo4j - Discover"));
    }

    @Test
    public void rich_relationship() {

        Filter mainTitleFilter = new Filter("mainTitle", EQUALS, "Neo4j");
        Book book = session.loadAll(Book.class, new Filters(mainTitleFilter))
            .iterator().next();

        Set<String> categories = book.getHasCategories().stream()
                .map(hasCategory -> hasCategory.getCategory().getTitle())
                .collect(Collectors.toSet());
        assertThat(categories, hasItems("Databases", "Programming"));

        Set<Double> matchingScores = book.getHasCategories().stream()
                .map(HasCategory::getMatching)
                .collect(Collectors.toSet());
        assertThat(matchingScores, hasItems(1.0, 0.3));
    }

    @Test
    public void transaction_example() {

        Filter emailFilter = new Filter("email", EQUALS, "nicolas@graphits.tech");
        try (Transaction tx = session.beginTransaction()) { // <1>
            Person nicolas = session.loadAll(Person.class,
                    new Filters(emailFilter)).iterator().next();
            nicolas.setName("Nicolas M");
            tx.rollback(); // <2>
            // or tx.commit()
        }
    }

    public void read_only_transaction() {

        try (Transaction tx = session.beginTransaction(Transaction.Type.READ_ONLY)) {
            // data access here
            tx.commit();
        }
    }

    public void example_bookmarks() {

        try (Transaction tx = session.beginTransaction()) { // <1>
            // db access
            tx.commit();
        }
        String bookmark = session.getLastBookmark();

        // let's assume the session is closed and the bookmark returned to the client application
        // time passes...
        // and the client sends a new call, passing the bookmark as a parameter

        session.withBookmark(bookmark);
        // the data read are guaranteed to be consistent with previous writes
    }

    @Test
    public void simple_transaction() {

        Filter emailFilter = new Filter("email", EQUALS, "nicolas@graphits.tech");
        Transaction tx = session.beginTransaction();
        try {
            Person nicolas = session.loadAll(Person.class,
                    new Filters(emailFilter)).iterator().next();
            nicolas.setName("Nicolas M");
            someMethodThrowingAnException();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
        assertEquals(Transaction.Status.ROLLEDBACK, tx.status());
        session = sessionFactory.openSession(); // <1>
        Person nicolas = session.loadAll(Person.class,
                new Filters(emailFilter)).iterator().next();
        assertEquals("Nicolas", nicolas.getName());
    }

    private void someMethodThrowingAnException() throws Exception {
        throw new Exception("test");
    }

    @Test
    public void example_aggregation() {

        Result result = session.query("MATCH (p:Person) -[:IS_AUTHOR_OF]-> (b:Book) "+
                "RETURN b.title as title, collect(p.name) as authors "
                , Collections.emptyMap()); // <1>

        Map<String, Object> record = result.queryResults().iterator().next(); // <2>

        assertEquals("Neo4j - A Graph Project Story", record.get("title"));
        String[] authors = (String[]) record.get("authors");
        Arrays.sort(authors);
        assertArrayEquals(new String[]{"Frank", "Nicolas", "Sylvain"}
                , authors);
    }

    @Test
    public void example_pagination() {

        Pagination paging = new Pagination(0, 10);
        SortOrder order = new SortOrder().add(SortOrder.Direction.ASC, "name");

        Iterable<Person> authors = session.loadAll(Person.class, order, paging);

        Iterator<Person> iterator = authors.iterator();
        assertEquals("Frank", iterator.next().getName());
        assertEquals("Nicolas", iterator.next().getName());
        assertEquals("Sylvain", iterator.next().getName());
    }

    @Test
    public void example_update_with_cypher() {

        Result result = session.query("MATCH (b:Book) SET b.title = b.mainTitle + b.subTitle"
                , Collections.emptyMap());

        QueryStatistics queryStatistics = result.queryStatistics();

        assertEquals(1, queryStatistics.getPropertiesSet());
        assertEquals(0, queryStatistics.getNodesCreated());
    }


    @Test
    public void concurrent_transactions_should_not_update_same_node() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        // start a tx, which updates a ndoe
        Transaction transaction = session.beginTransaction();
        Filter emailFilter = new Filter("email", EQUALS, "nicolas@graphits.tech");
        Person nicolas = session.loadAll(Person.class, new Filters(emailFilter)).iterator().next();
        nicolas.setName("nicolas1");
        session.save(nicolas);

        // in another thread, and another tx, try to update same node
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.submit(() -> {
            Session session2 = sessionFactory.openSession();
            Transaction transaction2 = session2.beginTransaction();
            assertNotEquals(transaction, transaction2);
            Person nicolas2 = session2.loadAll(Person.class, new Filters(emailFilter)).iterator().next();
            nicolas2.setName("nicolas2");
            latch.countDown();
            // stuck here because the other tx is still running
            session2.save(nicolas2);
            transaction2.commit();
            return 0;
        });

        // wait for the transaction in the thread to begin
        latch.await();
        Thread.sleep(100); // and give it time to do its save

        // check that the save in 2nd tx is not visible from here
        Person p1 = session.loadAll(Person.class, new Filters(emailFilter)).iterator().next();
        assertEquals("nicolas1", p1.getName());

        // end of main tx, 2nx tx can now proceed
        transaction.commit();

        // check thee 2nd tx finished properly
        pool.shutdown();
        boolean finished = pool.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertTrue(finished);

        // updates from 2nd tx are now visible
        Person saved = sessionFactory.openSession().loadAll(Person.class, new Filters(emailFilter)).iterator().next();
        assertEquals("nicolas2", saved.getName());
    }
}
