MATCH (proc:Procedure{name:$theProcedure})
MATCH (proc)-[*]->(task:Task{name:$theTaskStep})
MATCH (task)-[r]->(nextTask:Task)
RETURN task.name AS `Step in process`, type(r) AS Action