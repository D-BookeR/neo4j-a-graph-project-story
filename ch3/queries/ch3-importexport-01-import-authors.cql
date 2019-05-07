// Import authors from CSV file
LOAD CSV WITH HEADERS FROM "file:///authors.csv" AS author 
MERGE (person:Person {email: author.email, name: author.name})