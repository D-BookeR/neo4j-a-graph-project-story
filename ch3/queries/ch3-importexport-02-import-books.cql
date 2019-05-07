// Import books from CSV file
LOAD CSV WITH HEADERS FROM "file:///books.csv" AS bookitem
FIELDTERMINATOR ',' 
MERGE (book:Book 
{
    title: bookitem.title,
    mainTitle: bookitem.mainTitle,
    subTitle: bookitem.subTitle
})