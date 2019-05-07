MATCH (ord:Order{orderId:'20170330-1'})
MATCH (ord)-[r:STEP_IN_PROCESS]->(task:Task)
MATCH (task)-[status]->(nextTask:Task)
DELETE r
MERGE (ord)-[:STEP_IN_PROCESS]->(nextTask)
RETURN 'The step '+task.name+' is done. '
       + 'The process has status ' + nextTask.name
       AS Confirmation