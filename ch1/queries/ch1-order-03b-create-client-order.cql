:begin
MERGE (client:Person {email:"brian@graphits.tech"})
	ON CREATE SET client.name="Brian"
MERGE (order:Order {orderId:"20170330-1"})
MERGE (order)<-[:CHECKS_OUT]-(client)
WITH order, client
MATCH (p1:Product{isbn:"9782822702591"})
MERGE (order)-[:INCLUDES]->(p1)
WITH order, client, p1
MATCH (p2:Product{isbn:"9782822703826"})
MERGE (order)-[:INCLUDES]->(p2)
RETURN client.name, order.orderId, [p1,p2];
:commit