package com.homework.liveklasshomework.application.enrollment;

import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentActionCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentCreateCommand;
import com.homework.liveklasshomework.application.exception.CancellationPeriodExpiredException;
import com.homework.liveklasshomework.application.exception.DuplicateEnrollmentException;
import com.homework.liveklasshomework.application.exception.EnrollmentCapacityExceededException;
import com.homework.liveklasshomework.application.exception.ForbiddenException;
import com.homework.liveklasshomework.application.exception.InvalidStatusTransitionException;
import com.homework.liveklasshomework.application.exception.KlassClosedException;
import com.homework.liveklasshomework.application.exception.ResourceNotFoundException;
import com.homework.liveklasshomework.application.klass.KlassRepository;
import com.homework.liveklasshomework.domain.Enrollment;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final KlassRepository klassRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public Enrollment enroll(
            final EnrollmentCreateCommand command
    ) {
        final Klass klass = klassRepository.findByIdWithLock(command.klassId())
                .orElseThrow(() -> new ResourceNotFoundException("강의를 찾을 수 없습니다."));

        if (klass.status() == KlassStatus.CLOSED) {
            throw new KlassClosedException();
        }
        if (klass.status() != KlassStatus.OPEN) {
            throw new InvalidStatusTransitionException("OPEN 상태의 강의에만 신청할 수 있습니다.");
        }

        final boolean isDuplicate = enrollmentRepository.existsByKlassIdAndUserIdAndStatusIn(
                command.klassId(),
                command.userId(),
                List.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED)
        );
        if (isDuplicate) {
            throw new DuplicateEnrollmentException();
        }

        final long count = enrollmentRepository.countByKlassIdAndStatusNot(
                command.klassId(),
                EnrollmentStatus.CANCELLED
        );
        if (count >= klass.maxCapacity()) {
            throw new EnrollmentCapacityExceededException();
        }

        return enrollmentRepository.save(Enrollment.pending(command.klassId(), command.userId()));
    }

    @Override
    @Transactional
    public Enrollment confirm(
            final EnrollmentActionCommand command
    ) {
        final Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("수강 신청을 찾을 수 없습니다."));

        if (!enrollment.userId().equals(command.requesterId())) {
            throw new ForbiddenException();
        }
        if (enrollment.status() == EnrollmentStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("취소된 신청은 확정할 수 없습니다.");
        }
        if (enrollment.status() == EnrollmentStatus.CONFIRMED) {
            throw new InvalidStatusTransitionException("이미 확정된 신청입니다.");
        }

        return enrollmentRepository.save(enrollment.confirm());
    }

    @Override
    @Transactional
    public Enrollment cancel(
            final EnrollmentActionCommand command
    ) {
        final Enrollment enrollment = enrollmentRepository.findById(command.enrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("수강 신청을 찾을 수 없습니다."));

        if (!enrollment.userId().equals(command.requesterId())) {
            throw new ForbiddenException();
        }
        if (enrollment.status() == EnrollmentStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("이미 취소된 신청입니다.");
        }
        if (enrollment.status() == EnrollmentStatus.CONFIRMED) {
            if (enrollment.confirmedAt().plusDays(7).isBefore(LocalDateTime.now())) {
                throw new CancellationPeriodExpiredException();
            }
        }

        return enrollmentRepository.save(enrollment.cancel());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Enrollment> findMyEnrollments(
            final Long userId,
            final Pageable pageable
    ) {
        return enrollmentRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countNonCancelledEnrollments(
            final Long klassId
    ) {
        return enrollmentRepository.countByKlassIdAndStatusNot(
                klassId,
                EnrollmentStatus.CANCELLED
        );
    }
}
