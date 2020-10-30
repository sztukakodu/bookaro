package pl.sztukakodu.bookaro.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sztukakodu.bookaro.jpa.BaseEntity;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    private Long bookId;
    private int quantity;
}
