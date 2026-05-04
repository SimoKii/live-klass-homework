package com.homework.liveklasshomework.interfaces.klass.dto;

import com.homework.liveklasshomework.application.klass.dto.KlassDetailResult;
import com.homework.liveklasshomework.domain.KlassStatus;

import java.time.LocalDate;

public record KlassDetailResponse(
        Long id,
        Long creatorId,
        String title,
        String description,
        Long price,
        int maxCapacity,
        LocalDate startDate,
        LocalDate endDate,
        KlassStatus status,
        long currentEnrollmentCount
) {
    public static KlassDetailResponse from(
            final KlassDetailResult result
    ) {
        return new KlassDetailResponse(
                result.id(),
                result.creatorId(),
                result.title(),
                result.description(),
                result.price(),
                result.maxCapacity(),
                result.startDate(),
                result.endDate(),
                result.status(),
                result.currentEnrollmentCount()
        );
    }
}
