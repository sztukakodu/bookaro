package pl.sztukakodu.bookaro.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

}
