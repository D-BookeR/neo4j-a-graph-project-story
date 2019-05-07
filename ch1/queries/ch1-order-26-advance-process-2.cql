// find next task
MATCH (ord:Order{orderId:"20170330-1"})
MATCH (process:Process)<-[:IS_MANAGED_BY]-(ord)
MATCH (process)-[:IS_INSTANCE_OF]->(proc:Procedure)
MATCH (proc)-[*]->(task:Task{name:"Reception"})
MATCH (task)-[e]->(nextTask)
WHERE type(e)="CONFIRM_ORDER"

// Create the task instance
MATCH (process)-[pointer:STEP_IN_PROCESS]->(instanceInProgress:TaskInstance)
MERGE (nextInstance:TaskInstance{key: nextTask.name+' : '+ ord.orderId})
  -[:IS_ELEMENT_OF]->(process)
ON CREATE
  SET
    nextInstance = nextTask,
    nextInstance.key=nextTask.name+' : '+ ord.orderId
MERGE (instanceInProgress)-[:TO{action:'CONFIRM_ORDER'}]->(nextInstance)
RETURN instanceInProgress, nextInstance