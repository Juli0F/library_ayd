package com.ayd.library.controller;

import com.ayd.library.dto.user.CredentialsDto;
import com.ayd.library.exception.ServiceException;
import com.ayd.library.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<Void> getToken(@RequestBody CredentialsDto credentials) throws IOException, ServiceException {
        var token = authenticationService.signin(credentials);
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return ResponseEntity.ok().headers(headers).build();
    }
}
