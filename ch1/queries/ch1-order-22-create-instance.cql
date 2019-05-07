MATCH (proc:Procedure{name:"OrderProc"})
MATCH (ord:Order{orderId:"20170330-1"})
MERGE (process:Process{key:"Process "+ ord.orderId})
  <-[:IS_MANAGED_BY]-(ord)
MERGE (process)-[:IS_INSTANCE_OF]->(proc)
RETURN process