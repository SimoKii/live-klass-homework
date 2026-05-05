package com.homework.liveklasshomework.application.enrollment;

import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentActionCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentCreateCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentUsecaseImpl implements EnrollmentUsecase {

    private final EnrollmentService enrollmentService;

    @Override
    public EnrollmentResult enroll(
            final EnrollmentCreateCommand command
    ) {
        return EnrollmentResult.from(enrollmentService.enroll(command));
    }

    @Override
    public EnrollmentResult confirm(
            final EnrollmentActionCommand command
    ) {
        return EnrollmentResult.from(enrollmentService.confirm(command));
    }

    @Override
    public EnrollmentResult cancel(
            final EnrollmentActionCommand command
    ) {
        return EnrollmentResult.from(enrollmentService.cancel(command));
    }

    @Override
    public Page<EnrollmentResult> findMyEnrollments(
            final Long userId,
            final Pageable pageable
    ) {
        return enrollmentService.findMyEnrollments(userId, pageable)
                .map(EnrollmentResult::from);
    }
}
