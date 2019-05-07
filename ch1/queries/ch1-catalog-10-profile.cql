PROFILE
MATCH (publisher:Publisher{name:"Acme Publishing"})-[:CURRENT_CATALOG]->(catalog:Catalog)<-[:IS_AVAILABLE_IN]-(product:Product)<-[:PUBLISHED_AS]-(book:Book)-[:HAS_CATEGORY]->(category:Category{title:"Databases"}),
(author:Person)-[IS_AUTHOR_OF]->(book)
RETURN DISTINCT category.title, book.title, author.name
