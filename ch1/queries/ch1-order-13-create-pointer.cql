MATCH (ord:Order{orderId:"20170330-1"})
MATCH (task:Task{name:"Reception"})
MERGE (ord)-[:STEP_IN_PROCESS]->(task)
RETURN ord