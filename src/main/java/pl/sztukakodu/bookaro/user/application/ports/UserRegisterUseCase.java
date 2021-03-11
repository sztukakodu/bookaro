package pl.sztukakodu.bookaro.user.application.ports;

import pl.sztukakodu.bookaro.commons.Either;
import pl.sztukakodu.bookaro.user.domain.UserEntity;

public interface UserRegisterUseCase {

    RegisterResponse register(String username, String password);

    class RegisterResponse extends Either<String, UserEntity> {

        public RegisterResponse(boolean success, String left, UserEntity right) {
            super(success, left, right);
        }

        public static RegisterResponse success(UserEntity right) {
            return new RegisterResponse(true, null, right);
        }

        public static RegisterResponse failure(String error) {
            return new RegisterResponse(false, error, null);
        }
    }
}
