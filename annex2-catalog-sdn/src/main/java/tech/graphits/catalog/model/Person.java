package tech.graphits.catalog.model;

import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Person {

    @Id @GeneratedValue
    private Long id; // <1>

    @Property(name = "email") // <2>
    private String email;

    @Property(name = "name")
    private String name;

    @Relationship(type = "IS_AUTHOR_OF") // <3>
    private Set<Book> books = new HashSet<>();

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
