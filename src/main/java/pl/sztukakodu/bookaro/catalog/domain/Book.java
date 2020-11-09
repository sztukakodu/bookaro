package pl.sztukakodu.bookaro.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.sztukakodu.bookaro.jpa.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "authors")
@RequiredArgsConstructor
@Entity
public class Book extends BaseEntity {
    private String title;       // TODO-Darek: those are not unique, please fix this!
    private Integer year;
    private BigDecimal price;
    private Long coverId;
    private Long available;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable
    @JsonIgnoreProperties("books")
    private Set<Author> authors = new HashSet<>();

    public Book(String title, Integer year, BigDecimal price, Long available) {
        this.title = title;
        this.year = year;
        this.price = price;
        this.available = available;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void removeAuthors() {
        Book self = this;
        authors.forEach(author -> author.getBooks().remove(self));
        authors.clear();
    }
}
