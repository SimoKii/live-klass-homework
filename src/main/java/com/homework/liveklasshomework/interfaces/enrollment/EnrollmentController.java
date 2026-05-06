package com.homework.liveklasshomework.interfaces.enrollment;

import com.homework.liveklasshomework.application.enrollment.EnrollmentUsecase;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentActionCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentCreateCommand;
import com.homework.liveklasshomework.interfaces.common.CommonResponseDto;
import com.homework.liveklasshomework.interfaces.enrollment.dto.EnrollmentResponse;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentUsecase enrollmentUsecase;

    @PostMapping("/api/v1/classes/{classId}/enrollments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponseDto.SuccessResponseDto<EnrollmentResponse> enroll(
            @PathVariable final Long classId,
            @Positive @RequestHeader("X-User-Id") final Long userId
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                EnrollmentResponse.from(enrollmentUsecase.enroll(new EnrollmentCreateCommand(classId, userId)))
        );
    }

    @PatchMapping("/api/v1/enrollments/{enrollmentId}/confirm")
    public CommonResponseDto.SuccessResponseDto<EnrollmentResponse> confirm(
            @PathVariable final Long enrollmentId,
            @Positive @RequestHeader("X-User-Id") final Long userId
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                EnrollmentResponse.from(enrollmentUsecase.confirm(new EnrollmentActionCommand(enrollmentId, userId)))
        );
    }

    @PatchMapping("/api/v1/enrollments/{enrollmentId}/cancel")
    public CommonResponseDto.SuccessResponseDto<EnrollmentResponse> cancel(
            @PathVariable final Long enrollmentId,
            @Positive @RequestHeader("X-User-Id") final Long userId
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                EnrollmentResponse.from(enrollmentUsecase.cancel(new EnrollmentActionCommand(enrollmentId, userId)))
        );
    }

    @GetMapping("/api/v1/enrollments/me")
    public CommonResponseDto.SuccessResponseDto<Page<EnrollmentResponse>> findMyEnrollments(
            @Positive @RequestHeader("X-User-Id") final Long userId,
            final Pageable pageable
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                enrollmentUsecase.findMyEnrollments(userId, pageable).map(EnrollmentResponse::from)
        );
    }
}
