MATCH (p:Person)-[r:IS_AUTHOR_OF]->(b:Book)
RETURN p, b