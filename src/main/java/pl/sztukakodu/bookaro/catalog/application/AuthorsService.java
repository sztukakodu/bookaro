package pl.sztukakodu.bookaro.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sztukakodu.bookaro.catalog.application.port.AuthorsUseCase;
import pl.sztukakodu.bookaro.catalog.db.AuthorJpaRepository;
import pl.sztukakodu.bookaro.catalog.domain.Author;

import java.util.List;

@Service
@AllArgsConstructor
class AuthorsService implements AuthorsUseCase {
    private final AuthorJpaRepository repository;

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }
}
