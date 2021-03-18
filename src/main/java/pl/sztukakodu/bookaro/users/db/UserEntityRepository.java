package pl.sztukakodu.bookaro.users.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sztukakodu.bookaro.users.domain.UserEntity;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

}