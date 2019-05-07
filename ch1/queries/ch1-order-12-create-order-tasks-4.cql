FOREACH (next IN
  CASE n WHEN 0 THEN [tasks[n+1]] END |
    MERGE (previous)-[:CONFIRM_ORDER]->(next))
FOREACH (next IN
  CASE n WHEN 1 THEN [tasks[n+1]] END |
    MERGE (previous)-[:SEND_ORDER]->(next))))