// Load :HAS_CATEGORY relationships between books and categories 
LOAD CSV WITH HEADERS FROM "file:///books_categories.csv" AS bookcategory 
FIELDTERMINATOR ',' 
MATCH (book:Book {title: bookcategory.title}) 
MERGE (category:Category {title: bookcategory.category}) 
MERGE (book)-[:HAS_CATEGORY {matching: bookcategory.matching}]->(category)