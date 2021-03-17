package pl.sztukakodu.bookaro.users.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sztukakodu.bookaro.users.application.ports.UserRegistrationUseCase;

@Service
@AllArgsConstructor
class UserService implements UserRegistrationUseCase {

    @Transactional
    @Override
    public RegisterResponse register(String username, String password) {
        return RegisterResponse.failure("Not implemented yet");
    }
}