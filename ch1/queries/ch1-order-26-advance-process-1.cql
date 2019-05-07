// find next task
MATCH (ord:Order{orderId:"20170330-1"})
MATCH (process:Process)<-[:IS_MANAGED_BY]-(ord)
MATCH (process)-[:IS_INSTANCE_OF]->(proc:Procedure)
MATCH (proc)-[*]->(task:Task{name:"Reception"})
MATCH (task)-[e]->(nextTask)
WHERE type(e)="CONFIRM_ORDER"
RETURN nextTask