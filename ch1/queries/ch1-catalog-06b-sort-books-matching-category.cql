:begin
MATCH (category:Category{title:"Databases"})<-[r:HAS_CATEGORY]-(book:Book)
RETURN book.title
ORDER BY r.matching ASC;
:commit