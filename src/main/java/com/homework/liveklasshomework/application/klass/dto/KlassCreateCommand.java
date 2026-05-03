package com.homework.liveklasshomework.application.klass.dto;

import java.time.LocalDate;

public record KlassCreateCommand(
        Long creatorId,
        String title,
        String description,
        Long price,
        int maxCapacity,
        LocalDate startDate,
        LocalDate endDate
) {}
