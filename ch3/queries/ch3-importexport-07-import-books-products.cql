// Load :PUBLISHED_AS relationships between books and products 
LOAD CSV WITH HEADERS FROM "file:///books_products.csv" AS bookproduct 
FIELDTERMINATOR ',' 
MATCH (book:Book {title: bookproduct.title}) 
MERGE (product:Product {isbn: bookproduct.isbn, module: bookproduct.module}) 
MERGE (book)-[:PUBLISHED_AS]->(product)