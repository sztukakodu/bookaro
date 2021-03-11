package pl.sztukakodu.bookaro.user.application;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sztukakodu.bookaro.user.application.ports.UserRegisterUseCase;
import pl.sztukakodu.bookaro.user.db.UserEntityRepository;
import pl.sztukakodu.bookaro.user.domain.UserEntity;

import java.util.Optional;

@Service
@AllArgsConstructor
class UserService implements UserRegisterUseCase {
    private final UserEntityRepository repository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public RegisterResponse register(String username, String password) {
        Optional<UserEntity> user = repository.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity entity = new UserEntity(username, encoder.encode(password));
        return RegisterResponse.success(repository.save(entity));
    }
}
