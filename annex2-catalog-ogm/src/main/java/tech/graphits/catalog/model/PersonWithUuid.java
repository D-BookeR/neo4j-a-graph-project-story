package tech.graphits.catalog.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.id.UuidStrategy;

@NodeEntity
public class PersonWithUuid {

    @Id @GeneratedValue(strategy = UuidStrategy.class)
    private String uuid;

}
