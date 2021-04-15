package pl.sztukakodu.bookaro.security;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.sztukakodu.bookaro.users.db.UserEntityRepository;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(AdminConfig.class)
@Profile("!test")
@Slf4j
class BookaroSecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final UserEntityRepository userEntityRepository;
    private final AdminConfig config;
    private final String allowedOrigins;

    BookaroSecurityConfiguration(
        UserEntityRepository userEntityRepository,
        AdminConfig config,
        @Value("${app.security.allowedOrigins}") String allowedOrigins) {
        this.userEntityRepository = userEntityRepository;
        this.config = config;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configure CORS mapping with allowedOrigins: {}", allowedOrigins);
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(allowedOrigins);
    }

    @Bean
    User systemUser() {
        return config.adminUser();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/catalog/**", "/uploads/**", "/authors/**").permitAll()
            .mvcMatchers(HttpMethod.POST, "/orders", "/login", "/users").permitAll()
            .mvcMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        .and()
            .httpBasic()
        .and()
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @SneakyThrows
    private JsonUsernameAuthenticationFilter authenticationFilter() {
        JsonUsernameAuthenticationFilter filter = new JsonUsernameAuthenticationFilter();
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        BookaroUserDetailsService detailsService = new BookaroUserDetailsService(userEntityRepository, config);
        provider.setUserDetailsService(detailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
