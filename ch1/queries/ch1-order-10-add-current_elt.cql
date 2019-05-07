MATCH (list:List)
MATCH (s:Shelf) WHERE NOT (s)<-[:NEXT]-(:Shelf)
MERGE (list)-[:CURRENT_ELEMENT]->(s)