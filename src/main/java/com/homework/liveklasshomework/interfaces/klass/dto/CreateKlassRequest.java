package com.homework.liveklasshomework.interfaces.klass.dto;

import com.homework.liveklasshomework.application.klass.dto.KlassCreateCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateKlassRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 255, message = "title must not exceed 255 characters")
        String title,

        @NotBlank(message = "description must not be blank")
        @Size(max = 255, message = "description must not exceed 255 characters")
        String description,

        @NotNull(message = "price must not be null")
        @PositiveOrZero(message = "price must be 0 or positive")
        Long price,

        @NotNull(message = "maxCapacity must not be null")
        @Positive(message = "maxCapacity must be positive")
        Integer maxCapacity,

        @NotNull(message = "startDate must not be null")
        LocalDate startDate,

        @NotNull(message = "endDate must not be null")
        LocalDate endDate
) {
    @AssertTrue(message = "startDate must not be after endDate")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }

    public KlassCreateCommand toCommand(
            final Long creatorId
    ) {
        return new KlassCreateCommand(
                creatorId,
                title,
                description,
                price,
                maxCapacity,
                startDate,
                endDate
        );
    }
}
