package com.homework.liveklasshomework.infrastructure.jpa.enrollment;

import com.homework.liveklasshomework.domain.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentJpaEntity, Long> {

    boolean existsByKlassIdAndUserIdAndStatusIn(Long klassId, Long userId, List<EnrollmentStatus> statuses);

    long countByKlassIdAndStatusNot(Long klassId, EnrollmentStatus status);

    Page<EnrollmentJpaEntity> findByUserId(Long userId, Pageable pageable);
}
