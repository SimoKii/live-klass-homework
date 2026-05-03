package com.homework.liveklasshomework.application.klass;

import com.homework.liveklasshomework.application.enrollment.EnrollmentService;
import com.homework.liveklasshomework.application.klass.dto.KlassCreateCommand;
import com.homework.liveklasshomework.application.klass.dto.KlassDetailResult;
import com.homework.liveklasshomework.application.klass.dto.KlassResult;
import com.homework.liveklasshomework.application.klass.dto.KlassStatusUpdateCommand;
import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KlassUsecaseTest {

    @Mock
    private KlassService klassService;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private KlassUsecaseImpl klassUsecase;

    private Klass klass(final KlassStatus status) {
        return new Klass(
                1L,
                100L,
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                status
        );
    }

    @Test
    void 강의_생성_시_KlassResult_반환() {
        final KlassCreateCommand command = new KlassCreateCommand(
                100L,
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );
        when(klassService.create(command)).thenReturn(klass(KlassStatus.DRAFT));

        final KlassResult result = klassUsecase.create(command);

        assertThat(result.status()).isEqualTo(KlassStatus.DRAFT);
        assertThat(result.creatorId()).isEqualTo(100L);
    }

    @Test
    void 강의_상태_전이_시_KlassResult_반환() {
        final KlassStatusUpdateCommand command = new KlassStatusUpdateCommand(
                1L,
                100L,
                KlassStatus.OPEN
        );
        when(klassService.updateStatus(command)).thenReturn(klass(KlassStatus.OPEN));

        final KlassResult result = klassUsecase.updateStatus(command);

        assertThat(result.status()).isEqualTo(KlassStatus.OPEN);
    }

    @Test
    void 강의_목록_전체_조회_시_KlassResult_목록_반환() {
        when(klassService.findAll())
                .thenReturn(List.of(klass(KlassStatus.DRAFT), klass(KlassStatus.OPEN)));

        final List<KlassResult> result = klassUsecase.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).status()).isEqualTo(KlassStatus.DRAFT);
        assertThat(result.get(1).status()).isEqualTo(KlassStatus.OPEN);
    }

    @Test
    void 강의_상태별_목록_조회_시_KlassResult_목록_반환() {
        when(klassService.findAllByStatus(KlassStatus.OPEN))
                .thenReturn(List.of(klass(KlassStatus.OPEN)));

        final List<KlassResult> result = klassUsecase.findAllByStatus(KlassStatus.OPEN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(KlassStatus.OPEN);
    }

    @Test
    void 강의_상세_조회_시_CANCELLED_제외_수강_인원_포함_KlassDetailResult_반환() {
        when(klassService.findById(1L)).thenReturn(klass(KlassStatus.OPEN));
        when(enrollmentService.countNonCancelledEnrollments(1L)).thenReturn(5L);

        final KlassDetailResult result = klassUsecase.findById(1L);

        assertThat(result.currentEnrollmentCount()).isEqualTo(5L);
        assertThat(result.id()).isEqualTo(1L);
    }
}
