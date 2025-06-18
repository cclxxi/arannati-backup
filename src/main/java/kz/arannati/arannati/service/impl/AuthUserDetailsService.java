package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),                   // login is email
                user.getPassword(),                // password
                user.isActive(),                   // enabled - using active field
                true,                              // accountNonExpired
                true,                              // credentialsNonExpired
                true,                              // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }
}