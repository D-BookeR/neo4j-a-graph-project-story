// Load :OFFERED_BY and :CURRENT_CATALOG between catalogs and publishers 
LOAD CSV WITH HEADERS FROM "file:///catalogs_publishers.csv" AS catalogpublisher 
FIELDTERMINATOR ',' 
MATCH (catalog:Catalog {reference: catalogpublisher.reference}) 
MERGE (publisher:Publisher {id: catalogpublisher.id, name: catalogpublisher.name}) 
MERGE (catalog)-[:OFFERED_BY]->(publisher) 
FOREACH (n IN (
	CASE WHEN catalogpublisher.active_catalog IN ["YES"] THEN [1] else [] END) | 
	MERGE (publisher)-[:CURRENT_CATALOG]->(catalog)
 )