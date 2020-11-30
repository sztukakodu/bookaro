package pl.sztukakodu.bookaro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import pl.sztukakodu.bookaro.order.application.OrdersProperties;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({OrdersProperties.class})
public class BookaroOnlineStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookaroOnlineStoreApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
