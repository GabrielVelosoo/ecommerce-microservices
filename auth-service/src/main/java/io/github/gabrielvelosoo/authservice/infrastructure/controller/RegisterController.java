package io.github.gabrielvelosoo.authservice.infrastructure.controller;

import io.github.gabrielvelosoo.authservice.application.dto.register.SendRegisterCode;
import io.github.gabrielvelosoo.authservice.application.dto.register.ResendRegisterCode;
import io.github.gabrielvelosoo.authservice.application.dto.register.VerifyRegisterCode;
import io.github.gabrielvelosoo.authservice.application.usecase.RegisterResendCodeUseCase;
import io.github.gabrielvelosoo.authservice.application.usecase.RegisterSendCodeUseCase;
import io.github.gabrielvelosoo.authservice.application.usecase.RegisterVerifyCodeUseCase;
import io.github.gabrielvelosoo.authservice.application.validator.ValidationOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/register")
public class RegisterController {

    private final RegisterSendCodeUseCase registerSendCode;
    private final RegisterVerifyCodeUseCase registerVerifyCode;
    private final RegisterResendCodeUseCase registerResendCode;

    public RegisterController(RegisterSendCodeUseCase registerSendCode, RegisterVerifyCodeUseCase registerVerifyCode, RegisterResendCodeUseCase registerResendCode) {
        this.registerSendCode = registerSendCode;
        this.registerVerifyCode = registerVerifyCode;
        this.registerResendCode = registerResendCode;
    }

    @PostMapping(value = "/send-code")
    public ResponseEntity<Void> sendRegisterCode(@RequestBody @Validated(ValidationOrder.class) SendRegisterCode request) {
        registerSendCode.execute(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(value = "/verify-code")
    public ResponseEntity<Void> verifyRegisterCode(@RequestBody @Validated(ValidationOrder.class) VerifyRegisterCode request) {
        registerVerifyCode.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/resend-code")
    public ResponseEntity<Void> resendRegisterCode(@RequestBody @Validated(ValidationOrder.class) ResendRegisterCode request) {
        registerResendCode.execute(request);
        return ResponseEntity.accepted().build();
    }
}
