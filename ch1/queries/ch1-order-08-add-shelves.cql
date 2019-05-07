MATCH (list:List)
WITH list, ["Fresh Food","Canned Food","Drinks"] as shelves
FOREACH (s IN shelves |
  CREATE (shelf:Shelf{name:s})-[:IS_ELEMENT_OF]->(list))
RETURN list,shelves