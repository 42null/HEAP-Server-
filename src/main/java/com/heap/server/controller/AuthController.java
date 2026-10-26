package com.heap.server.controller;

import com.heap.server.dto.LoginRequest;
import com.heap.server.dto.TokenResponse;
import com.heap.server.security.SecurityProperties;
import com.heap.server.security.TokenService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;
    private final SecurityProperties props;

    public AuthController(TokenService tokenService, SecurityProperties props) {
        this.tokenService = tokenService;
        this.props = props;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        // Non-short-circuit & so both comparisons always run (constant-time).
        boolean ok = matches(request.username(), props.username())
                   & matches(request.password(), props.password());
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new TokenResponse(tokenService.generateToken(request.username()), "Bearer", tokenService.ttlSeconds());
    }

    private static boolean matches(String provided, String expected) {
        return MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }
}
