package com.homework.liveklasshomework.application.klass.dto;

import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

public record KlassDetailResult(
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
    public KlassDetailResult {
        if (Objects.isNull(id)) throw new IllegalArgumentException("'id' must not be null");
        if (Objects.isNull(creatorId)) throw new IllegalArgumentException("'creatorId' must not be null");
        if (!StringUtils.hasText(title)) throw new IllegalArgumentException("'title' must not be blank");
        if (!StringUtils.hasText(description)) throw new IllegalArgumentException("'description' must not be blank");
        if (Objects.isNull(price)) throw new IllegalArgumentException("'price' must not be null");
        if (maxCapacity <= 0) throw new IllegalArgumentException("'maxCapacity' must be positive");
        if (Objects.isNull(startDate)) throw new IllegalArgumentException("'startDate' must not be null");
        if (Objects.isNull(endDate)) throw new IllegalArgumentException("'endDate' must not be null");
        if (Objects.isNull(status)) throw new IllegalArgumentException("'status' must not be null");
        if (currentEnrollmentCount < 0) throw new IllegalArgumentException("'currentEnrollmentCount' must be 0 or positive");
    }

    public static KlassDetailResult of(
            final Klass klass,
            final long currentEnrollmentCount
    ) {
        return new KlassDetailResult(
                klass.id(),
                klass.creatorId(),
                klass.title(),
                klass.description(),
                klass.price(),
                klass.maxCapacity(),
                klass.startDate(),
                klass.endDate(),
                klass.status(),
                currentEnrollmentCount
        );
    }
}
