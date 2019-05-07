LOAD CSV WITH HEADERS FROM "file:///authors_books.csv" AS authorbook FIELDTERMINATOR ','
MATCH (person:Person {email: authorbook.email})
MATCH (book:Book {title: authorbook.title})
MERGE (person)-[:IS_AUTHOR_OF]->(book)