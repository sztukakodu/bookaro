package pl.sztukakodu.bookaro.commons;

import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public class Either<L, R> {
    private final boolean success;
    private final L left;
    private final R right;

    public <T> T handle(Function<R, T> onSuccess, Function<L, T> onError) {
        if(success) {
            return onSuccess.apply(right);
        } else {
            return onError.apply(left);
        }
    }
}
