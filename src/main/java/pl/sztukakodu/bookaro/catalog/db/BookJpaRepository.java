package pl.sztukakodu.bookaro.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.sztukakodu.bookaro.catalog.domain.Book;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(String firstName, String lastName);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
        " WHERE lower(a.firstName) LIKE lower(concat('%', :name,'%')) " +
        " OR lower(a.lastName) LIKE lower(concat('%', :name,'%')) ")
    List<Book> findByAuthorName(@Param("name") String name);

    @Query(
        " SELECT b FROM Book b JOIN b.authors a " +
            " WHERE lower(b.title) LIKE lower(concat('%', :title,'%')) " +
            " AND (" +
            "   lower(a.firstName) LIKE lower(concat('%', :name,'%')) " +
            " OR lower(a.lastName) LIKE lower(concat('%', :name,'%')) " +
            ")"
    )
    List<Book> findByTitleAndAuthorName(@Param("title") String title, @Param("name") String name);
}
