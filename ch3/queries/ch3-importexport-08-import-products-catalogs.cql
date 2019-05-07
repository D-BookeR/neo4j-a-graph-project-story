// Load :IS_AVAILABLE_IN relationships between products and catalogs 
LOAD CSV WITH HEADERS FROM "file:///products_catalogs.csv" AS productcatalog 
FIELDTERMINATOR ',' 
MATCH (product:Product {isbn: productcatalog.isbn}) 
MERGE (catalog:Catalog {reference: productcatalog.reference, year: toInt(productcatalog.year)}) 
MERGE (product)-[:IS_AVAILABLE_IN]->(catalog)