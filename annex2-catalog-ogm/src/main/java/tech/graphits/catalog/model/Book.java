package tech.graphits.catalog.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class Book {

    @Id @GeneratedValue
    private Long id;
    private String mainTitle;
    private String subTitle;
    private String title;

    @Relationship(type = "PUBLISHED_AS", direction = Relationship.OUTGOING)
    private Set<Product> products;

    @Relationship(type = "HAS_CATEGORY", direction = Relationship.OUTGOING)
    private Set<HasCategory> hasCategories;

    public Book() {
    }

    public Book(String title, String mainTitle, String subTitle) {
        this.title = title;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> product) {
        this.products = product;
    }

    public Set<HasCategory> getHasCategories() {
        return hasCategories;
    }

    public void setHasCategories(Set<HasCategory> hasCategory) {
        this.hasCategories = hasCategory;
    }
}
