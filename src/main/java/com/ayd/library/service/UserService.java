package com.ayd.library.service;

import com.ayd.library.exception.NotFoundException;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.model.User;
import com.ayd.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.ayd.library.model.UserInfoDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findById(Long id) throws ServiceException {
        return this.userRepository.findById(id).orElseThrow(()->new NotFoundException(String.format("This user sale with id:%s dont exists",id)));
    }

    public User findByUsername(String username) throws ServiceException {
        return this.userRepository.findByUsername(username).orElseThrow(()->new NotFoundException(String.format("This user sale with username:%s dont exists",username)));
    }


    public User createUser(String username, String rawPassword, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        userRepository.save(user);
        return user;
    }
    public void encodeExistingPasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
    }


}
