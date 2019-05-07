:begin
MATCH (ord:Order{orderId:"20170330-1"})
WITH ord,
     ['Reception','Preparation','Shipping'] as taskNames
FOREACH (n IN RANGE(0, SIZE(taskNames)-1) |
  FOREACH (taskName IN [taskNames[n]] |
    MERGE (task:Task{name:taskName,order:n})-[:IS_ELEMENT_OF]->(ord)))
WITH  ord
MATCH (task:Task)-[:IS_ELEMENT_OF]->(ord)
WITH ord, task ORDER BY task.order
WITH ord, COLLECT(task) AS tasks
FOREACH (n IN RANGE(0, SIZE(tasks)-1) |
  FOREACH (previous IN [tasks[n]] |
    FOREACH (next IN
      CASE n WHEN 0  THEN [tasks[n+1]] END | MERGE (previous)-[:CONFIRM_ORDER]->(next))
    FOREACH (next IN
      CASE n WHEN 1  THEN [tasks[n+1]] END | MERGE (previous)-[:SEND_ORDER]->(next))))
RETURN ord
:commit