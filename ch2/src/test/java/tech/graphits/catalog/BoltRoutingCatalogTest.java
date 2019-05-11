package tech.graphits.catalog;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

import java.io.IOException;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * These tests that demonstrates the bolt+routing usage
 *
 * @author sroussy
 */
public class BoltRoutingCatalogTest {

    // To be changed according to database setup
    private static final String BOLT_URL = "bolt+routing://localhost:7687";

    // To be changed according to database setup
    private static final String USER = "neo4j";

    // To be changed according to database setup
    private static final String PASSWORD = "neo4j";

    private Driver driver;

    /**
     * BOLT driver instantiated
     */
    @Before
    public void setup() {
        final AuthToken token = AuthTokens.basic(USER, PASSWORD);
        driver = GraphDatabase.driver(BOLT_URL, token);
    }

    /**
     * Test implicit transactions
     */
    @Test
    public void testImplicitTransaction() {
        try (Session sessionBolt = driver.session()) {
            sessionBolt.run("MERGE (p:Person {name: $name})", parameters("name", "Nicolas"));
        }

        try (Session sessionBolt = driver.session()) {
            final StatementResult sr = sessionBolt.run("MATCH (p:Person {name: $name}) RETURN p", parameters("name", "Nicolas"));
            while (sr.hasNext()) {
                Record resultRow = sr.next();
                Value pResultColumn = resultRow.get("p");
                Node node = pResultColumn.asNode();
                Value value = node.get("name");
                System.out.println(value.asString());

                Assert.assertEquals("Nicolas", node.get("name").asString());
            }
        }
    }

    /**
     * Test explicit transactions
     */
    @Test
    public void testExplicitTransaction() {
        String bookmark;

        try (Session sessionBolt = driver.session()) {
            try (Transaction tx = sessionBolt.beginTransaction()) {
                tx.run("MERGE (p:Person {name: $name})", parameters("name", "Nicolas"));
                tx.success();

                bookmark = sessionBolt.lastBookmark();
            }
        }

        try (Session sessionBolt = driver.session(bookmark)) {
            try (Transaction tx = sessionBolt.beginTransaction()) {
                final StatementResult sr = tx.run("MATCH (p:Person {name: $name}) RETURN p", parameters("name", "Nicolas"));
                while (sr.hasNext()) {
                    Record resultRow = sr.next();
                    Value pResultColumn = resultRow.get("p");
                    Node node = pResultColumn.asNode();
                    Value valeur = node.get("name");
                    System.out.println(valeur.asString());

                    Assert.assertEquals("Nicolas", node.get("name").asString());
                }
            }
        }
    }

    /**
     * Bookmark use case
     */
    @Test
    public void testTransactionFunctions() {
        String bookmark;
        final String name = "Nicolas";
        try (Session sessionBolt = driver.session()) {
            sessionBolt.writeTransaction(new TransactionWork<Integer>() {
                @Override
                public Integer execute(Transaction tx) {
                    return createPerson(tx, name);
                }
            });
            bookmark = sessionBolt.lastBookmark();
            System.out.println(bookmark);
        }


        try (Session sessionBolt = driver.session(bookmark)) {
            Node node = sessionBolt.readTransaction(new TransactionWork<Node>() {
                @Override
                public Node execute(Transaction tx) {
                    return findPerson(tx, name);
                }
            });
            System.out.println(node.get("name").asString());
            Assert.assertEquals("Nicolas", node.get("name").asString());

        }
    }

    /**
     * Create a person into the graph
     *
     * @param tx Transaction
     * @param name Person name
     * @return ignore
     */
    private static int createPerson(Transaction tx, String name) {
        tx.run("MERGE (p:Person {name: $name})", parameters("name", name)).consume();
        return 1;
    }

    /**
     * Find a person node from the graph
     *
     * @param tx Transaction
     * @param name Person name
     * @return The person found
     */
    private static Node findPerson(Transaction tx, String name) {
        Node node = null;
        final StatementResult sr = tx.run("MATCH (p:Person {name: $name}) RETURN p", parameters("name", name));
        if (sr.hasNext()) {
            node = sr.next().get("p").asNode();
        }
        return node;
    }


    /**
     * Release the BOLT driver
     */
    @After
    public void closeDriver() {
        if (driver != null) driver.close();
    }
}
