package com.homework.liveklasshomework.domain;

import org.springframework.util.StringUtils;

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
        assert creatorId != null : "'creatorId' must not be null";
        assert StringUtils.hasText(title) : "'title' must not be blank";
        assert StringUtils.hasText(description) : "'description' must not be blank";
        assert price != null && price >= 0 : "'price' must be 0 or positive";
        assert maxCapacity > 0 : "'maxCapacity' must be positive";
        assert startDate != null : "'startDate' must not be null";
        assert endDate != null : "'endDate' must not be null";
        assert !startDate.isAfter(endDate) : "'startDate' must not be after 'endDate'";
        assert status != null : "'status' must not be null";
    }
}