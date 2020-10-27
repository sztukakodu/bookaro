package pl.sztukakodu.bookaro.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recipient {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;
}
