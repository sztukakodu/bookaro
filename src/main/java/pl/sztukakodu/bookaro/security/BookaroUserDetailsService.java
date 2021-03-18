package pl.sztukakodu.bookaro.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.sztukakodu.bookaro.users.db.UserEntityRepository;

@AllArgsConstructor
class BookaroUserDetailsService implements UserDetailsService {

    private final UserEntityRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository
            .findByUsernameIgnoreCase(username)
            .map(UserEntityDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
