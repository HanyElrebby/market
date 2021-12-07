package com.market.service;

import com.market.config.Constants;
import com.market.config.RandomString;
import com.market.domain.Authority;
import com.market.domain.User;
import com.market.repository.AuthorityRepository;
import com.market.repository.UserRepository;
import com.market.security.AuthoritiesConstants;
import com.market.security.SecurityUtils;
import com.market.service.dto.AdminUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    private AuthorityRepository authorityRepository;

    private PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            AuthorityRepository authorityRepository,
            PasswordEncoder passwordEncoder

    ) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User RegisterUser(AdminUserDTO userDTO, String password) {
        userRepository
                .findUserByEmailIgnoreCase(userDTO.getEmail().toLowerCase())
                .ifPresent(
                        existingUser ->
                        {
                            boolean removed = removeNonActivatedUser(existingUser);
                            if (!removed) {
                                throw new EmailAlreadyUsedException();
                            }
                        });
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername().toLowerCase());

        // new user gets initially a generated password
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encryptedPassword);
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setLangKey(userDTO.getLangKey());

        // new user is not active
        newUser.setActivated(false);

        // new user gets registration key
        RandomString randomString = new RandomString();
        newUser.setActivationKey(randomString.nextString());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public Optional<User> activateRegistration(String key){
        log.debug("Activating user for activation key {}", key);
        return userRepository
                .findUserByActivationKey(key)
                .map(
                        user -> {
                            user.setActivated(true);
                            user.setActivationKey(null);
                            userRepository.save(user);
                            return user;
                        });
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    public User createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        if(userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        if(userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
        else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        RandomString randomString = new RandomString();
        user.setActivationKey(randomString.nextString());

        user.setResetKey(randomString.nextString());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional
                .of(userRepository.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(
                        user -> {
                            user.setUsername(userDTO.getUsername());
                            if (userDTO.getEmail() != null) {
                                user.setEmail(userDTO.getEmail().toLowerCase());
                            }
                            user.setActivated(userDTO.isActivated());
                            user.setLangKey(userDTO.getLangKey());
                            Set<Authority> managedAuthorities = user.getAuthorities();
                            managedAuthorities.clear();
                            userDTO
                                    .getAuthorities()
                                    .stream()
                                    .map(authorityRepository::findById)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .forEach(managedAuthorities::add);
                            log.debug("Changed Information for User: {}", user);
                            return user;
                        }
                )
                .map(AdminUserDTO::new);
    }

    public boolean deleteUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    public void updateUser(String username, String email, String langKey) {
        SecurityUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findUserByEmailIgnoreCase)
                .ifPresent(
                        user -> {
                            user.setUsername(username);
                            if (email != null) {
                                user.setEmail(email.toLowerCase());
                            }
                            user.setLangKey(langKey);
                            log.debug("Changed Information for User: {}", user);
                        }
                );
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByEmailIgnoreCase);
    }
}
