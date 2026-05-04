package com.homework.liveklasshomework.interfaces.klass.dto;

import com.homework.liveklasshomework.application.klass.dto.KlassResult;
import com.homework.liveklasshomework.domain.KlassStatus;

import java.time.LocalDate;

public record KlassResponse(
        Long id,
        Long creatorId,
        String title,
        String description,
        Long price,
        int maxCapacity,
        LocalDate startDate,
        LocalDate endDate,
        KlassStatus status
) {
    public static KlassResponse from(
            final KlassResult result
    ) {
        return new KlassResponse(
                result.id(),
                result.creatorId(),
                result.title(),
                result.description(),
                result.price(),
                result.maxCapacity(),
                result.startDate(),
                result.endDate(),
                result.status()
        );
    }
}
