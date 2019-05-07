MATCH (n:Order)-[INCLUDES]->(p:Product)-[r:IS_AVAILABLE_IN]->(c:Catalog)
RETURN sum (r.price)