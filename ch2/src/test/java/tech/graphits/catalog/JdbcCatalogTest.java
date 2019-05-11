package tech.graphits.catalog;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Tests based on JDBC and BOLT
 *
 * @author sroussy
 */
public class JdbcCatalogTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // To replace with your host url
    private static final String JDBC_BOLT_URL = "jdbc:neo4j:bolt://localhost";

    // To replace with your user
    private static final String USER = "neo4j";

    // To replace with your password
    private static final String PASSWORD = "root";

    /**
     * Simple connection to Neo4j with JDBC
     */
    @Test
    public void testJdbcConnection() {

        try (Connection con = DriverManager.getConnection(JDBC_BOLT_URL, USER, PASSWORD)) {

        } catch (SQLException e) {
            e.printStackTrace();

            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test with JDBC queries : create + find a person
     */
    @Test
    public void testJdbcQuery() {

        final String writeQuery = "MERGE (p:Person {name: {1}})";

        try (Connection con = DriverManager.getConnection(JDBC_BOLT_URL, USER, PASSWORD)) {

            PreparedStatement pstmt = con.prepareStatement(writeQuery);
            pstmt.setString(1, "Patricia");
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

            Assert.fail(e.getMessage());
        }

        final String readQuery = "MATCH (p:Person {name:{1}}) RETURN p.name AS name";
        try (Connection con = DriverManager.getConnection(JDBC_BOLT_URL, USER, PASSWORD)) {

            PreparedStatement pstmt = con.prepareStatement(readQuery);
            pstmt.setString(1, "Patricia");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("person: " + rs.getString("name"));
                }
            } catch (SQLException err) {
                err.printStackTrace();
                Assert.fail(err.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();

            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test JDBC queries: create and retrieve a person with Map converter
     */
    @Test
    public void testJdbcObject() {

        final String writeQuery = "MERGE (p:Person {name: {1}})";

        try (Connection con = DriverManager.getConnection(JDBC_BOLT_URL, USER, PASSWORD)) {

            PreparedStatement pstmt = con.prepareStatement(writeQuery);
            pstmt.setString(1, "Patricia");
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

            Assert.fail(e.getMessage());
        }

        final String readQuery = "MATCH (p:Person {name:{1}}) RETURN p";
        try (Connection con = DriverManager.getConnection(JDBC_BOLT_URL, USER, PASSWORD)) {

            PreparedStatement pstmt = con.prepareStatement(readQuery);
            pstmt.setString(1, "Patricia");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> node = (Map<String, Object>) rs.getObject("p");
                    System.out.println("person (Map) : " + node);
                }
            } catch (SQLException err) {
                err.printStackTrace();
                Assert.fail(err.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();

            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test JDBC queries: create and retrieve a person with PErson object mapping
     */
    @Test
    public void testJdbcperson() {

        final String writeQuery = "MERGE (p:Person {name: {1}})";

        try (Connection con = DriverManager.getConnection(JDBC_BOLT_URL, USER, PASSWORD)) {

            PreparedStatement pstmt = con.prepareStatement(writeQuery);
            pstmt.setString(1, "Patricia");
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

            Assert.fail(e.getMessage());
        }

        final String readQuery = "MATCH (p:Person {name:{1}}) RETURN p";
        try (Connection con = DriverManager.getConnection(JDBC_BOLT_URL, USER, PASSWORD)) {

            PreparedStatement pstmt = con.prepareStatement(readQuery);
            pstmt.setString(1, "Patricia");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> node = (Map<String, Object>) rs.getObject("p");
                    Person person = MAPPER.convertValue(node, Person.class);
                    System.out.println("Object Person: " + person.getName() + " labels :" + person.getLabels());
                }
            } catch (SQLException err) {
                err.printStackTrace();
                Assert.fail(err.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();

            Assert.fail(e.getMessage());
        }
    }

    /**
     * Person bean
     *
     * @author sroussy
     */
    public static class Person {

        private Long _id;
        private List<String> _labels;
        private String name;

        public Long getId() {
            return _id;
        }

        public void setId(Long _id) {
            this._id = _id;
        }

        public List<String> getLabels() {
            return _labels;
        }

        public void setLabels(List<String> _labels) {
            this._labels = _labels;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
