MATCH (n:Order)-[INCLUDES]->(p:Product)-[r:IS_AVAILABLE_IN]->(c:Catalog)
RETURN p.module as module, r.price as price
UNION
MATCH (n:Order)-[INCLUDES]->(p:Product)-[r:IS_AVAILABLE_IN]->(c:Catalog)
RETURN 'Total' as module, sum (r.price) as price