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
}