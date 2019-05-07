MATCH (ord:Order{orderId:"20170330-1"})
MATCH (order)-[r:STEP_IN_PROCESS]->(task:Task)
MATCH (task)<-[]-(previousTask:Task)
DELETE r
MERGE (ord)-[:STEP_IN_PROCESS]->(previousTask)
RETURN "The step "+task.name
       +" is cancelled.  The order reverts to the task : "
       +previousTask.name AS Confirmation