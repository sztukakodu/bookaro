package pl.sztukakodu.bookaro.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class BookaroSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    User systemAdmin() {
        return new User(
            "system_admin",
            "",
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/catalog/**", "/uploads/**", "/authors/**").permitAll()
            .mvcMatchers(HttpMethod.POST, "/orders").permitAll()
            .anyRequest().authenticated()
        .and()
            .httpBasic()
        .and()
            .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("marek@example.org")
            .password("{noop}xxx")
            .roles("USER")
            .and()
            .withUser("admin")
            .password("{noop}xxx")
            .roles("ADMIN");
    }
}
