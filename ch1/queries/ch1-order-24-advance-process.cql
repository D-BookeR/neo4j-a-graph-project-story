MATCH (proc:Procedure{name:"OrderProc"})
MATCH (proc)-[*]->(task:Task{name:"Reception"})
MATCH (task)-[r]->(nextTask:Task)
RETURN task.name AS `Step in process`, type(r) AS Action