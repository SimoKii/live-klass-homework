package com.homework.liveklasshomework.interfaces.enrollment.dto;

import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentResult;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long klassId,
        Long userId,
        EnrollmentStatus status,
        LocalDateTime confirmedAt
) {
    public static EnrollmentResponse from(
            final EnrollmentResult result
    ) {
        return new EnrollmentResponse(
                result.id(),
                result.klassId(),
                result.userId(),
                result.status(),
                result.confirmedAt()
        );
    }
}
