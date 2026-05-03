package com.homework.liveklasshomework.application.enrollment;

import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentActionCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentCreateCommand;
import com.homework.liveklasshomework.domain.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {

    Enrollment enroll(final EnrollmentCreateCommand command);

    Enrollment confirm(final EnrollmentActionCommand command);

    Enrollment cancel(final EnrollmentActionCommand command);

    Page<Enrollment> findMyEnrollments(final Long userId, final Pageable pageable);

    long countNonCancelledEnrollments(final Long klassId);
}
