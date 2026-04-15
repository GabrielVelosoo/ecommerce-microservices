package io.github.gabrielvelosoo.authservice.application.validator;

import jakarta.validation.GroupSequence;

@GroupSequence({ ValidateNotBlank.class, ValidateOthers.class })
public interface ValidationOrder {
}
