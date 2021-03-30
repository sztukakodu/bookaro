package pl.sztukakodu.bookaro.catalog.web;

import lombok.Value;

import java.math.BigDecimal;

@Value
class RestBook {
    Long id;
    String title;
    Integer year;
    BigDecimal price;
    String coverUrl;
    Boolean available;
}
