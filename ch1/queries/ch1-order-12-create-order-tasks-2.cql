WITH ord,
     ['Reception','Preparation','Shipping'] as taskNames
FOREACH (n IN RANGE(0, SIZE(taskNames)-1) |
  FOREACH (taskName IN [taskNames[n]] |
    MERGE (task:Task{name:taskName,order:n})-[:IS_ELEMENT_OF]->(ord)))