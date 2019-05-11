package tech.graphits.catalog.model;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "HAS_CATEGORY") // <1>
public class HasCategory {

    @Id @GeneratedValue
    private Long id;

    @StartNode  // <2>
    private Book book;

    @EndNode  // <2>
    private Category category;

    private Double matching;

    // getters & setters

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getMatching() {
        return matching;
    }

    public void setMatching(Double matching) {
        this.matching = matching;
    }
}
