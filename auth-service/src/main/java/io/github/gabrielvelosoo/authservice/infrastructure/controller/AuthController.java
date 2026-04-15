package io.github.gabrielvelosoo.authservice.infrastructure.controller;

import io.github.gabrielvelosoo.authservice.application.dto.email.CheckEmailRequest;
import io.github.gabrielvelosoo.authservice.application.dto.email.CheckEmailResponse;
import io.github.gabrielvelosoo.authservice.application.usecase.CheckEmailUseCase;
import io.github.gabrielvelosoo.authservice.application.validator.ValidationOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final CheckEmailUseCase checkEmail;

    public AuthController(CheckEmailUseCase checkEmail) {
        this.checkEmail = checkEmail;
    }

    @PostMapping(value = "/check-email")
    public ResponseEntity<CheckEmailResponse> checkEmail(@RequestBody @Validated(ValidationOrder.class) CheckEmailRequest request) {
        CheckEmailResponse response = checkEmail.execute(request);
        return ResponseEntity.ok(response);
    }
}
