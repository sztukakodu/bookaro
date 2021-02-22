package pl.sztukakodu.bookaro.clock;

import java.time.Duration;
import java.time.LocalDateTime;

public interface Clock {
    LocalDateTime now();

    class Fake implements Clock {
        private LocalDateTime time;

        public Fake(LocalDateTime time) {
            this.time = time;
        }

        public Fake() {
            this(LocalDateTime.now());
        }

        @Override
        public LocalDateTime now() {
            return time;
        }

        public void tick(Duration duration) {
            time = time.plus(duration);
        }
    }
}
