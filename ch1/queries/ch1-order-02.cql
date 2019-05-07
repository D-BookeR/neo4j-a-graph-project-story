MATCH (p1:Product{isbn:"9782822702591"})-[r1:IS_AVAILABLE_IN]->(c:Catalog)
SET r1.price=25
WITH p1, r1, c
MATCH (p2:Product{isbn:"9782822703826"})-[r2:IS_AVAILABLE_IN]->(:Catalog)
SET r2.price=35 
RETURN c.reference as Catalog, p1.module, r1.price, p2.module, r2.price