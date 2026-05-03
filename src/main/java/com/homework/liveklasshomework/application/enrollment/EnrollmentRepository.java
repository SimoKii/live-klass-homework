package com.homework.liveklasshomework.application.enrollment;

import com.homework.liveklasshomework.domain.Enrollment;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {

    Enrollment save(final Enrollment enrollment);

    Optional<Enrollment> findById(final Long id);

    boolean existsByKlassIdAndUserIdAndStatusIn(final Long klassId, final Long userId, final List<EnrollmentStatus> statuses);

    long countByKlassIdAndStatusNot(final Long klassId, final EnrollmentStatus status);

    Page<Enrollment> findByUserId(final Long userId, final Pageable pageable);
}
