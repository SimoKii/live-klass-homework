package com.homework.liveklasshomework.infrastructure.jpa.enrollment.impl;

import com.homework.liveklasshomework.application.enrollment.EnrollmentRepository;
import com.homework.liveklasshomework.domain.Enrollment;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
import com.homework.liveklasshomework.infrastructure.jpa.enrollment.EnrollmentJpaEntity;
import com.homework.liveklasshomework.infrastructure.jpa.enrollment.EnrollmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmentJpaRepository enrollmentJpaRepository;

    @Override
    public Enrollment save(
            final Enrollment enrollment
    ) {
        final EnrollmentJpaEntity savedEntity = enrollmentJpaRepository.save(
                EnrollmentJpaEntity.from(enrollment)
        );

        return savedEntity.toDomain();
    }

    @Override
    public Optional<Enrollment> findById(
            final Long id
    ) {
        return enrollmentJpaRepository.findById(id)
                .map(EnrollmentJpaEntity::toDomain);
    }

    @Override
    public boolean existsByKlassIdAndUserIdAndStatusIn(
            final Long klassId,
            final Long userId,
            final List<EnrollmentStatus> statuses
    ) {
        return enrollmentJpaRepository.existsByKlassIdAndUserIdAndStatusIn(klassId, userId, statuses);
    }

    @Override
    public long countByKlassIdAndStatusNot(
            final Long klassId,
            final EnrollmentStatus status
    ) {
        return enrollmentJpaRepository.countByKlassIdAndStatusNot(klassId, status);
    }

    @Override
    public Page<Enrollment> findByUserId(
            final Long userId,
            final Pageable pageable
    ) {
        return enrollmentJpaRepository.findByUserId(userId, pageable)
                .map(EnrollmentJpaEntity::toDomain);
    }
}
