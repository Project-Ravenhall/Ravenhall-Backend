package dev.ravenhall.user.adapters.in.web;

import dev.ravenhall.user.adapters.in.web.dto.mapper.AuthMapper;
import dev.ravenhall.user.adapters.in.web.dto.request.LoginRequest;
import dev.ravenhall.user.adapters.in.web.dto.request.SignupRequest;
import dev.ravenhall.user.adapters.in.web.dto.response.AuthResponse;
import dev.ravenhall.user.application.AuthResult;
import dev.ravenhall.user.application.UserAuthService;
import dev.ravenhall.user.application.ports.in.commands.LoginCommand;
import dev.ravenhall.user.application.ports.in.commands.SignupCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;
    private final AuthMapper authMapper;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupRequest request) {
        AuthResult result = userAuthService.signup(
                new SignupCommand(request.username(), request.password(), request.displayName())
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authMapper.toResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResult result = userAuthService.login(
                new LoginCommand(request.username(), request.password())
        );
        return ResponseEntity
                .ok(authMapper.toResponse(result));
    }
}