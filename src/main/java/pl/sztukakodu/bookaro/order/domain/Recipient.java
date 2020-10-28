package pl.sztukakodu.bookaro.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipient {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;

    public Recipient(String name, String phone, String street, String city, String zipCode, String email) {
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.email = email;
    }
}
