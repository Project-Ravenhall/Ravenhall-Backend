package dev.ravenhall.user.application.ports.in;

import dev.ravenhall.user.application.AuthResult;
import dev.ravenhall.user.application.ports.in.commands.LoginCommand;
import dev.ravenhall.user.application.ports.in.commands.SignupCommand;

public interface UserAuthUseCase {
    AuthResult signup(SignupCommand signupCommand);
    AuthResult login(LoginCommand loginCommand);
}
