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
        if (klassId == null) throw new IllegalArgumentException("'klassId' must not be null");
        if (userId == null) throw new IllegalArgumentException("'userId' must not be null");
        if (status == null) throw new IllegalArgumentException("'status' must not be null");
    }
}