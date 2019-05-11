## Source code for annex 2 on Spring Data Neo4j

See the JUnit test cases in `src/test/java` as a starting point.

This repo contains an simple example Spring Boot application.

It expects a running Neo4j instance (on the standard ports) with test data imported from `src/test/resources/test_data.cql`.

You can use also use the following docker commands from this directory to do the setup:

`docker run --name book-neo4j --rm --publish=7474:7474 --publish=7687:7687 --env NEO4J_AUTH=none neo4j:3.5.5`

`cat src/test/resources/test_data.cql | docker exec -i book-neo4j /var/lib/neo4j/bin/cypher-shell`

Then head to the `SDNTest` Junit test case.