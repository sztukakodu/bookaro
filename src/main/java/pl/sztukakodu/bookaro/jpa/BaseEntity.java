package pl.sztukakodu.bookaro.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String uuid = UUID.randomUUID().toString();
}
