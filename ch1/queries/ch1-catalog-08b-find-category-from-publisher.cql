:begin
MATCH (publisher:Publisher{name:"Acme Publishing"})-[:CURRENT_CATALOG]->(catalog:Catalog)<-[:IS_AVAILABLE_IN]-(product:Product)<-[:PUBLISHED_AS]-(book:Book)-[:HAS_CATEGORY]->(category:Category)
RETURN DISTINCT category.title;
:commit