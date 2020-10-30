package pl.sztukakodu.bookaro.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN FETCH b.authors")
    List<Book> findAllEager();

    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(String firstName, String lastName);

    List<Book> findByTitleStartsWithIgnoreCase(String title);

    @Query(
        " SELECT b FROM Book b JOIN b.authors a " +
            " WHERE " +
            " lower(a.firstName) LIKE lower(concat('%', :name,'%')) " +
            " OR lower(a.lastName) LIKE lower(concat('%', :name,'%')) "
    )
    List<Book> findByAuthor(@Param("name") String name);
}
