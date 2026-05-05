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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private KlassRepository klassRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Klass openKlass(final int maxCapacity) {
        return new Klass(
                1L,
                100L,
                "Java 입문",
                "설명",
                50000L,
                maxCapacity,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                KlassStatus.OPEN
        );
    }

    private Enrollment pending() {
        return new Enrollment(
                1L,
                1L,
                200L,
                EnrollmentStatus.PENDING,
                null
        );
    }

    private Enrollment confirmed(final LocalDateTime confirmedAt) {
        return new Enrollment(
                1L,
                1L,
                200L,
                EnrollmentStatus.CONFIRMED,
                confirmedAt
        );
    }

    @Test
    void 존재하지_않는_강의_수강_신청_시_예외_발생() {
        when(klassRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentCreateCommand(1L, 200L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 수강_신청_성공() {
        when(klassRepository.findByIdWithLock(1L)).thenReturn(Optional.of(openKlass(30)));
        when(enrollmentRepository.existsByKlassIdAndUserIdAndStatusIn(
                eq(1L), eq(200L), anyList()
        )).thenReturn(false);
        when(enrollmentRepository.countByKlassIdAndStatusNot(1L, EnrollmentStatus.CANCELLED))
                .thenReturn(5L);
        when(enrollmentRepository.save(any()))
                .thenReturn(new Enrollment(1L, 1L, 200L, EnrollmentStatus.PENDING, null));

        final Enrollment result = enrollmentService.enroll(new EnrollmentCreateCommand(1L, 200L));

        assertThat(result.status()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(result.userId()).isEqualTo(200L);
    }

    @Test
    void 정원_초과_신청_거부() {
        when(klassRepository.findByIdWithLock(1L)).thenReturn(Optional.of(openKlass(1)));
        when(enrollmentRepository.existsByKlassIdAndUserIdAndStatusIn(
                anyLong(), anyLong(), anyList()
        )).thenReturn(false);
        when(enrollmentRepository.countByKlassIdAndStatusNot(1L, EnrollmentStatus.CANCELLED))
                .thenReturn(1L);

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentCreateCommand(1L, 200L)))
                .isInstanceOf(EnrollmentCapacityExceededException.class);
    }

    @Test
    void 중복_신청_거부() {
        when(klassRepository.findByIdWithLock(1L)).thenReturn(Optional.of(openKlass(30)));
        when(enrollmentRepository.existsByKlassIdAndUserIdAndStatusIn(
                eq(1L), eq(200L), eq(List.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED))
        )).thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentCreateCommand(1L, 200L)))
                .isInstanceOf(DuplicateEnrollmentException.class);
    }

    @Test
    void DRAFT_상태_강의_수강_신청_거부() {
        final Klass draftKlass = new Klass(
                1L,
                100L,
                "test",
                "desc",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                KlassStatus.DRAFT
        );
        when(klassRepository.findByIdWithLock(1L)).thenReturn(Optional.of(draftKlass));

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentCreateCommand(1L, 200L)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void CLOSED_상태_강의_수강_신청_거부() {
        final Klass closedKlass = new Klass(
                1L,
                100L,
                "test",
                "desc",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                KlassStatus.CLOSED
        );
        when(klassRepository.findByIdWithLock(1L)).thenReturn(Optional.of(closedKlass));

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentCreateCommand(1L, 200L)))
                .isInstanceOf(KlassClosedException.class);
    }

    @Test
    void 수강_취소_후_재신청_성공() {
        when(klassRepository.findByIdWithLock(1L)).thenReturn(Optional.of(openKlass(30)));
        when(enrollmentRepository.existsByKlassIdAndUserIdAndStatusIn(
                eq(1L), eq(200L), eq(List.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED))
        )).thenReturn(false);
        when(enrollmentRepository.countByKlassIdAndStatusNot(1L, EnrollmentStatus.CANCELLED))
                .thenReturn(0L);
        when(enrollmentRepository.save(any()))
                .thenReturn(new Enrollment(2L, 1L, 200L, EnrollmentStatus.PENDING, null));

        final Enrollment result = enrollmentService.enroll(new EnrollmentCreateCommand(1L, 200L));

        assertThat(result.status()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(result.userId()).isEqualTo(200L);
    }

    @Test
    void 존재하지_않는_수강_신청_결제_확정_시_예외_발생() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.confirm(new EnrollmentActionCommand(1L, 200L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 결제_확정_PENDING에서_CONFIRMED로_전이_성공() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(pending()));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final Enrollment result = enrollmentService.confirm(new EnrollmentActionCommand(1L, 200L));

        assertThat(result.status()).isEqualTo(EnrollmentStatus.CONFIRMED);
    }

    @Test
    void 결제_확정_시_확정_일시_기록() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(pending()));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        enrollmentService.confirm(new EnrollmentActionCommand(1L, 200L));

        final ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(captor.capture());
        assertThat(captor.getValue().confirmedAt()).isNotNull();
    }

    @Test
    void 결제_확정_타인_거부() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(pending()));

        assertThatThrownBy(() -> enrollmentService.confirm(new EnrollmentActionCommand(1L, 999L)))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 결제_확정_CANCELLED에서_CONFIRMED로_전이_거부() {
        when(enrollmentRepository.findById(1L)).thenReturn(
                Optional.of(new Enrollment(1L, 1L, 200L, EnrollmentStatus.CANCELLED, null))
        );

        assertThatThrownBy(() -> enrollmentService.confirm(new EnrollmentActionCommand(1L, 200L)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void 결제_확정_CONFIRMED에서_재확정_거부() {
        when(enrollmentRepository.findById(1L)).thenReturn(
                Optional.of(confirmed(LocalDateTime.now()))
        );

        assertThatThrownBy(() -> enrollmentService.confirm(new EnrollmentActionCommand(1L, 200L)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void 존재하지_않는_수강_신청_취소_시_예외_발생() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.cancel(new EnrollmentActionCommand(1L, 200L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 수강_취소_PENDING에서_CANCELLED로_전이_성공() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(pending()));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final Enrollment result = enrollmentService.cancel(new EnrollmentActionCommand(1L, 200L));

        assertThat(result.status()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    void 수강_취소_결제_확정일_7일_이내_성공() {
        final LocalDateTime confirmedAt = LocalDateTime.now().minusDays(3);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(confirmed(confirmedAt)));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final Enrollment result = enrollmentService.cancel(new EnrollmentActionCommand(1L, 200L));

        assertThat(result.status()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    void 수강_취소_결제_확정일_7일_이내_경계값_성공() {
        final LocalDateTime confirmedAt = LocalDateTime.now().minusDays(7).plusSeconds(1);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(confirmed(confirmedAt)));
        when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final Enrollment result = enrollmentService.cancel(new EnrollmentActionCommand(1L, 200L));

        assertThat(result.status()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    void 수강_취소_결제_확정일_7일_초과_거부() {
        final LocalDateTime confirmedAt = LocalDateTime.now().minusDays(8);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(confirmed(confirmedAt)));

        assertThatThrownBy(() -> enrollmentService.cancel(new EnrollmentActionCommand(1L, 200L)))
                .isInstanceOf(CancellationPeriodExpiredException.class);
    }

    @Test
    void 수강_취소_타인_거부() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(pending()));

        assertThatThrownBy(() -> enrollmentService.cancel(new EnrollmentActionCommand(1L, 999L)))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 수강_취소_CANCELLED에서_재취소_거부() {
        when(enrollmentRepository.findById(1L)).thenReturn(
                Optional.of(new Enrollment(1L, 1L, 200L, EnrollmentStatus.CANCELLED, null))
        );

        assertThatThrownBy(() -> enrollmentService.cancel(new EnrollmentActionCommand(1L, 200L)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void 내_신청_목록_본인_신청_조회_성공() {
        final Page<Enrollment> page = new PageImpl<>(List.of(pending()));
        when(enrollmentRepository.findByUserId(eq(200L), any())).thenReturn(page);

        final Page<Enrollment> result = enrollmentService.findMyEnrollments(200L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).userId()).isEqualTo(200L);
    }

    @Test
    void 내_신청_목록_페이지네이션_성공() {
        final Page<Enrollment> page = new PageImpl<>(List.of(), PageRequest.of(1, 5), 10);
        when(enrollmentRepository.findByUserId(eq(200L), any())).thenReturn(page);

        final Page<Enrollment> result = enrollmentService.findMyEnrollments(200L, PageRequest.of(1, 5));

        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(1);
    }

    @Test
    void 취소_제외_수강_인원_카운트_성공() {
        when(enrollmentRepository.countByKlassIdAndStatusNot(1L, EnrollmentStatus.CANCELLED))
                .thenReturn(5L);

        final long count = enrollmentService.countNonCancelledEnrollments(1L);

        assertThat(count).isEqualTo(5L);
    }
}
