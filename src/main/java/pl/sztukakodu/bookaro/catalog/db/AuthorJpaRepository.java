package pl.sztukakodu.bookaro.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
}
