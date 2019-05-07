MATCH (s:Shelf)-[:IS_ELEMENT_OF]->(:List)
WITH s ORDER BY s.name
WITH COLLECT(s) AS shelves
FOREACH (n IN RANGE(0, SIZE(shelves)-2) |
  FOREACH (previous IN [shelves[n]] |
    FOREACH (following IN [shelves[n+1]] |
      MERGE (previous)-[:NEXT]->(following))))