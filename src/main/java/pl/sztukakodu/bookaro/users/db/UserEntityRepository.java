package pl.sztukakodu.bookaro.users.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sztukakodu.bookaro.users.domain.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

}