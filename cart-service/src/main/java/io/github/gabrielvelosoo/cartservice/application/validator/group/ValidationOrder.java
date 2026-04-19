package io.github.gabrielvelosoo.cartservice.application.validator.group;

import jakarta.validation.GroupSequence;

@GroupSequence({ ValidateNotNull.class, ValidateOthers.class })
public interface ValidationOrder {
}
