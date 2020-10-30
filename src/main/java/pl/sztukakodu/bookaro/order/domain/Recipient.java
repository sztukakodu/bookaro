package pl.sztukakodu.bookaro.order.domain;

import lombok.*;
import pl.sztukakodu.bookaro.jpa.BaseEntity;

import javax.persistence.Entity;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipient extends BaseEntity {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;
}
