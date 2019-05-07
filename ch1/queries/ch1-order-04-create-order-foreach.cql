MERGE (client:Person{email:"brian@graphits.tech"})
	ON CREATE SET client.name="Brian"
MERGE (order:Order {orderId:"20170330-1"})
MERGE (order)<-[:CHECKS_OUT]-(client)
WITH order, client, ["9782822702591","9782822703826"] as products
FOREACH (product IN products |
  MERGE (order)-[:INCLUDES]->(:Product{isbn:product}))