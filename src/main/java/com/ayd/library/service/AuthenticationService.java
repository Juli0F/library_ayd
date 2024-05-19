package com.ayd.library.service;

import com.ayd.library.dto.user.CredentialsDto;
import com.ayd.library.dto.user.UserRequestDto;
import com.ayd.library.exception.DuplicatedEntityException;
import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.User;
import com.ayd.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    public User signup(UserRequestDto userRequestDto) throws DuplicatedEntityException {
        var duplicatedUserByUsername = userRepository.findByUsername(userRequestDto.username());
        if (duplicatedUserByUsername.isPresent())
            throw new DuplicatedEntityException("User with username already exists");

        var duplicatedUserByEmail = userRepository.findByEmail(userRequestDto.email());
        if (duplicatedUserByEmail.isPresent())
            throw new DuplicatedEntityException("User with email already exists");

        var newUser = userRequestDto.toUser();
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);

    }

    public User updateUser(Long id, UserRequestDto userRequestDto) throws ServiceException {
        var userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var duplicatedUsername = userRepository.findByUsernameAndUserIdNot(userRequestDto.username(), id);
        if (duplicatedUsername.isPresent())
            throw new DuplicatedEntityException("User with username already exists");

        var duplicatedEmail = userRepository.findByEmailAndUserIdNot(userRequestDto.email(), id);
        if (duplicatedEmail.isPresent())
            throw new DuplicatedEntityException("User with email already exists");

        userToUpdate.setName(userRequestDto.name());
        userToUpdate.setEmail(userRequestDto.email());
        userToUpdate.setUsername(userRequestDto.username());
        userToUpdate.setPassword(encoder.encode(userRequestDto.password()));

        return userRepository.save(userToUpdate);
    }

    public String signin(CredentialsDto credentials) throws ServiceException, IOException {

        var user = userRepository.findByUsername(credentials.username())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!(user.getStatus() == 1))
            throw new ServiceException("User is disabled");

        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.username(), credentials.password()));
        if (authentication != null && authentication.isAuthenticated()) {
            return jwtService.generateToken(user);
        }
        throw new UsernameNotFoundException("Invalid user credentials");

    }
}
