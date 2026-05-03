package com.homework.liveklasshomework.application.enrollment;

import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentActionCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentCreateCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentUsecase {

    EnrollmentResult enroll(final EnrollmentCreateCommand command);

    EnrollmentResult confirm(final EnrollmentActionCommand command);

    EnrollmentResult cancel(final EnrollmentActionCommand command);

    Page<EnrollmentResult> findMyEnrollments(final Long userId, final Pageable pageable);
}
