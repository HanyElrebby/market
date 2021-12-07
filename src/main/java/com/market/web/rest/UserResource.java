package com.market.web.rest;

import com.market.domain.User;
import com.market.repository.UserRepository;
import com.market.security.AuthoritiesConstants;
import com.market.service.EmailAlreadyUsedException;
import com.market.service.MailService;
import com.market.service.UserService;
import com.market.service.dto.AdminUserDTO;
import com.market.web.rest.errors.BadRequestAlertException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    private UserRepository userRepository;

    private UserService userService;

    private MailService mailService;

    public UserResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<User> createUser(@Valid @RequestBody AdminUserDTO userDTO) throws URISyntaxException {
        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idExists");
        }
        else if (userRepository.findUserByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        }
        else {
        User newUser = userService.createUser(userDTO);
        mailService.sendCreationEmail(newUser);
        return ResponseEntity.created(new URI("users/" + newUser.getEmail())).body(newUser);
        }
    }

}
