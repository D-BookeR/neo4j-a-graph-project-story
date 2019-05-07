MATCH (p:Person)-[r:IS_AUTHOR_OF]->(l:Book)
RETURN p.email, type(r), l.title