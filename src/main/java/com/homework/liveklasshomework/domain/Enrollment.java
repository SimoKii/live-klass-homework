package com.homework.liveklasshomework.domain;

import java.time.LocalDateTime;

public record Enrollment(
        Long id,
        Long klassId,
        Long userId,
        EnrollmentStatus status,
        LocalDateTime confirmedAt
) {
    public Enrollment {
        assert klassId != null : "'klassId' must not be null";
        assert userId != null : "'userId' must not be null";
        assert status != null : "'status' must not be null";
    }

    public static Enrollment pending(
            final Long klassId,
            final Long userId
    ) {
        return new Enrollment(
                null,
                klassId,
                userId,
                EnrollmentStatus.PENDING,
                null
        );
    }

    public Enrollment confirm() {
        return new Enrollment(
                id,
                klassId,
                userId,
                EnrollmentStatus.CONFIRMED,
                LocalDateTime.now()
        );
    }

    public Enrollment cancel() {
        return new Enrollment(
                id,
                klassId,
                userId,
                EnrollmentStatus.CANCELLED,
                confirmedAt
        );
    }
}