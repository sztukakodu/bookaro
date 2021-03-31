package pl.sztukakodu.bookaro.catalog.web;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Value
class RestBook {
    Long id;
    String title;
    Integer year;
    BigDecimal price;
    String coverUrl;
    Boolean available;
    Set<RestAuthor> authors;
}
