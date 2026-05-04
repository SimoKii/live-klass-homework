package com.homework.liveklasshomework.interfaces.klass.dto;

import com.homework.liveklasshomework.application.klass.dto.KlassStatusUpdateCommand;
import com.homework.liveklasshomework.domain.KlassStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateKlassStatusRequest(
        @NotNull(message = "status must not be null")
        KlassStatus status
) {
    public KlassStatusUpdateCommand toCommand(
            final Long klassId,
            final Long requesterId
    ) {
        return new KlassStatusUpdateCommand(
                klassId,
                requesterId,
                status
        );
    }
}
