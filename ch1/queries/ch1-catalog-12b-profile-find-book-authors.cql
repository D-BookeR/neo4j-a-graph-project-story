:begin
PROFILE
MATCH (publisher:Publisher{name:"Acme Publishing"})
    USING INDEX publisher:Publisher(name)
MATCH (publisher)-[:CURRENT_CATALOG]->(catalog:Catalog)
MATCH (catalog)<-[:IS_AVAILABLE_IN]-(product:Product)
MATCH (product)<-[:PUBLISHED_AS]-(book:Book)
MATCH (author:Person)-[IS_AUTHOR_OF]->(book)
WITH DISTINCT author,book
MATCH (category:Category{title:"Databases"})<-[:HAS_CATEGORY]-(book)
RETURN category.title,author.name,book.title;
:commit