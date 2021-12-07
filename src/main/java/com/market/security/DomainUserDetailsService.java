package com.market.security;

import com.market.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.market.domain.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//Authenticate a user from the database.
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        logger.debug("Authenticating {}", email);
         return userRepository
                 .findOneWithAuthoritiesByEmailIgnoreCase(email)
                 .map(user -> createSpringSecurityUser(email, user))
                 .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " was not found in the database"));
   }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String toLowerCaseEmail, User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User " + toLowerCaseEmail + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user
            .getAuthorities()
            .stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }
}

