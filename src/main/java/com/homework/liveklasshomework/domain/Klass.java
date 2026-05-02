package com.homework.liveklasshomework.domain;

import java.time.LocalDate;

public record Klass(
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
    public Klass {
        if (creatorId == null) throw new IllegalArgumentException("'creatorId' must not be null");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("'title' must not be blank");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("'description' must not be blank");
        if (price == null || price < 0) throw new IllegalArgumentException("'price' must be 0 or positive");
        if (maxCapacity <= 0) throw new IllegalArgumentException("'maxCapacity' must be positive");
        if (startDate == null) throw new IllegalArgumentException("'startDate' must not be null");
        if (endDate == null) throw new IllegalArgumentException("'endDate' must not be null");
        if (status == null) throw new IllegalArgumentException("'status' must not be null");
    }
}