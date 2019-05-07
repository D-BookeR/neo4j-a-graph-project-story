MATCH (ord:Order{orderId:"20170330-1"})
MATCH (process:Process)<-[:IS_MANAGED_BY]-(order)
MATCH (process)-[:IS_INSTANCE_OF]->(proc:Procedure)
MATCH (proc)-[:START]->(task:Task)
MERGE (taskInstance:TaskInstance{key:task.name+" : "+ ord.orderId})
  -[:IS_ELEMENT_OF]->(process)
ON CREATE SET
  taskInstance = task,
  taskInstance.key=task.name+" : "+ ord.orderId
MERGE (process)-[:STEP_IN_PROCESS]->(taskInstance)
RETURN process, taskInstance 