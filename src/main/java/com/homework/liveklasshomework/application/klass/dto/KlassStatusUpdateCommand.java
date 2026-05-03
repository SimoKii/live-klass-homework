package com.homework.liveklasshomework.application.klass.dto;

import com.homework.liveklasshomework.domain.KlassStatus;

public record KlassStatusUpdateCommand(
        Long klassId,
        Long requesterId,
        KlassStatus targetStatus
) {}
