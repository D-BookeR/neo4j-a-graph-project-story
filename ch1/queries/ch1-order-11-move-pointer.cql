MATCH (list:List)-[p:CURRENT_ELEMENT]->(current:Shelf)
MATCH (current)-[:NEXT]->(following:Shelf)
DELETE p
MERGE (list)-[:CURRENT_ELEMENT]->(following)
RETURN current