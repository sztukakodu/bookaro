package pl.sztukakodu.bookaro.security;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.sztukakodu.bookaro.user.db.UserEntityRepository;

@AllArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(AdminUserConfig.class)
class BookaroSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserEntityRepository repository;
    private final AdminUserConfig config;

    @Bean
    User adminUser() {
        return config.adminUser();
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        UserDetailsService userDetailsService = new BookaroUserDetailsService(repository, config);
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder());
        return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable();
        http
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/catalog/**", "/uploads/**", "/authors/**").permitAll()
            .mvcMatchers(HttpMethod.POST, "/orders", "/login").permitAll()
            .anyRequest().authenticated()
        .and()
            .httpBasic()
        .and()
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
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
}
