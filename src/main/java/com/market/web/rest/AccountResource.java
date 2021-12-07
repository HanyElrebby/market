package com.market.web.rest;

import com.market.domain.User;
import com.market.repository.UserRepository;
import com.market.security.SecurityUtils;
import com.market.service.EmailAlreadyUsedException;
import com.market.service.MailService;
import com.market.service.UserService;
import com.market.service.dto.AdminUserDTO;
import com.market.web.rest.errors.InvalidPasswordException;
import com.market.web.rest.vm.ManagedUserVM;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(AccountResource.class);

    private UserRepository userRepository;

    private UserService userService;

    private MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }
    @PostMapping("/register")
    public void RegisterAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthValid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.RegisterUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
    }

    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if(!user.isPresent()){
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    @PostMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        logger.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
                .getUserWithAuthorities()
                .map(AdminUserDTO::new)
                .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }


    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils
                .getCurrentUserLogin()
                .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findUserByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getEmail().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        userService.updateUser(
                userDTO.getUsername(),
                userDTO.getEmail(),
                userDTO.getLangKey()
        );
    }



    public boolean isPasswordLengthValid(String password) {
        return (
                StringUtils.isEmpty(password) ||
                password.length() < ManagedUserVM.PASSWORD_MAX_LENGTH ||
                password.length() > ManagedUserVM.PASSWORD_MIN_LENGTH
        );
    }
}
