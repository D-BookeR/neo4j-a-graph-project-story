:begin
MATCH (category:Category{title:"Databases"})
WITH category
CREATE (book2:Book {title:"A sample book"}),
	(book2)-[r:HAS_CATEGORY{matching:0.85}]->(category)
WITH category
MATCH (category)<-[r:HAS_CATEGORY]-(book:Book)
RETURN book.title 	ORDER BY r.matching DESC;
:commit