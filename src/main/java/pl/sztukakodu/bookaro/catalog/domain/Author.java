package pl.sztukakodu.bookaro.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@ToString(exclude = "books")
public class Author {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "authors")
    @JsonIgnoreProperties("authors")
    private Set<Book> books;

    @CreatedDate
    private LocalDateTime createdAt;

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
