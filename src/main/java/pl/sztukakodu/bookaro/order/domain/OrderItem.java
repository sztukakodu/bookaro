package pl.sztukakodu.bookaro.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sztukakodu.bookaro.catalog.domain.Book;
import pl.sztukakodu.bookaro.jpa.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    private int quantity;
}
