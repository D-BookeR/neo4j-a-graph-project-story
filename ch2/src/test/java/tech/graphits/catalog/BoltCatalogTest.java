package tech.graphits.catalog;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.harness.junit.Neo4jRule;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * BOLT protocol based test
 *
 * @author sroussy
 */
public class BoltCatalogTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFixture("MERGE (p:Person {name: 'Nicolas'})");

    // To be changed according to database setup
    private static final String USER = "neo4j";

    // To be changed to database setup
    private static final String PASSWORD = "neo4j";

    private final AuthToken token = AuthTokens.basic(USER, PASSWORD);

    /**
     * Test implicit transactions
     */
    @Test
    public void testImplicitTransaction() {

        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), token)) {
            // Here the transaction is implicit
            try (Session sessionBolt = driver.session()) {
                sessionBolt.run("MERGE (p:Person {name: $name})", parameters("name", "Nicolas"));
            }

            try (Session sessionBolt = driver.session()) {
                final StatementResult sr = sessionBolt.run("MATCH (p:Person {name: $name}) RETURN p", parameters("name", "Nicolas"));
                while (sr.hasNext()) {
                    Record resultRow = sr.next();
                    Value pResultColumn = resultRow.get("p");
                    Node node = pResultColumn.asNode();
                    Value nameValue = node.get("name");
                    System.out.println(nameValue.asString());

                    Assert.assertEquals("Nicolas", nameValue.asString());
                }
            }
        }
    }

    /**
     * Test explicit transactions
     */
    @Test
    public void testExplicitTransaction() {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), token)) {
            try (Session sessionBolt = driver.session()) {
                // Here, the transaction is explicit
                try (Transaction tx = sessionBolt.beginTransaction()) {
                    tx.run("MERGE (p:Person {name: $name})", parameters("name", "Nicolas"));
                    tx.success();
                }
            }

            try (Session sessionBolt = driver.session()) {
                final StatementResult sr = sessionBolt.run("MATCH (p:Person {name: $name}) RETURN p", parameters("name", "Nicolas"));
                while (sr.hasNext()) {
                    Record resultRow = sr.next();
                    Value pResultColumn = resultRow.get("p");
                    Node node = pResultColumn.asNode();
                    Value nameValue = node.get("name");
                    System.out.println(nameValue.asString());

                    Assert.assertEquals("Nicolas", node.get("name").asString());
                }
            }
        }
    }

    /**
     * Test transactional functions
     */
    @Test
    public void testTransactionFunctions() {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), token)) {
            final String name = "Nicolas";
            try (Session sessionBolt = driver.session()) {
                sessionBolt.writeTransaction(new TransactionWork<Integer>() {
                    @Override
                    public Integer execute(Transaction tx) {
                        return createPerson(tx, name);
                    }
                });
            }

            try (Session sessionBolt = driver.session()) {
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
    }

    /**
     * Create a prson into the graph
     *
     * @param tx Transaction
     * @param name Person name
     * @return ignore
     */
    private static int createPerson(Transaction tx, String name) {
        tx.run("MERGE (p:Person {name: $name})", parameters("name", name));
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

}
