package com.homework.liveklasshomework.application.enrollment.dto;

import com.homework.liveklasshomework.domain.Enrollment;
import com.homework.liveklasshomework.domain.EnrollmentStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public record EnrollmentResult(
        Long id,
        Long klassId,
        Long userId,
        EnrollmentStatus status,
        LocalDateTime confirmedAt // PENDING 상태에서 null 허용
) {
    public EnrollmentResult {
        if (Objects.isNull(id)) throw new IllegalArgumentException("'id' must not be null");
        if (Objects.isNull(klassId)) throw new IllegalArgumentException("'klassId' must not be null");
        if (Objects.isNull(userId)) throw new IllegalArgumentException("'userId' must not be null");
        if (Objects.isNull(status)) throw new IllegalArgumentException("'status' must not be null");
    }

    public static EnrollmentResult from(
            final Enrollment enrollment
    ) {
        return new EnrollmentResult(
                enrollment.id(),
                enrollment.klassId(),
                enrollment.userId(),
                enrollment.status(),
                enrollment.confirmedAt()
        );
    }
}
