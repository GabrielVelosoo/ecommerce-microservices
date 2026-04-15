package io.github.gabrielvelosoo.productservice.application.validator.group;

import jakarta.validation.GroupSequence;

@GroupSequence({ ValidateNotBlank.class, ValidateOthers.class })
public interface ValidationOrder {
}
