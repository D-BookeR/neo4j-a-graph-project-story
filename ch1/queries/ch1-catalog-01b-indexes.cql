:begin
CREATE CONSTRAINT ON (b:Book) ASSERT b.title IS UNIQUE;
CREATE INDEX ON :Book(mainTitle);
CREATE INDEX ON :Book(subTitle);
CREATE CONSTRAINT ON (c:Category) ASSERT c.title IS UNIQUE;
CREATE CONSTRAINT ON (pr:Product) ASSERT pr.isbn IS UNIQUE;
CREATE INDEX ON :Product(module);
CREATE CONSTRAINT ON (ca:Catalog) ASSERT ca.reference IS UNIQUE;
CREATE CONSTRAINT ON (pub:Publisher) ASSERT pub.id IS UNIQUE;
CREATE INDEX ON :Publisher(name);
:commit