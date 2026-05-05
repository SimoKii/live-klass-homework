package com.homework.liveklasshomework.application.enrollment;

import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentActionCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentCreateCommand;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentResult;
import com.homework.liveklasshomework.domain.Enrollment;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentUsecaseTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentUsecaseImpl enrollmentUsecase;

    private Enrollment pending() {
        return new Enrollment(
                1L,
                1L,
                200L,
                EnrollmentStatus.PENDING,
                null
        );
    }

    @Test
    void 수강_신청_시_EnrollmentResult_반환() {
        final EnrollmentCreateCommand command = new EnrollmentCreateCommand(1L, 200L);
        when(enrollmentService.enroll(command)).thenReturn(pending());

        final EnrollmentResult result = enrollmentUsecase.enroll(command);

        assertThat(result.status()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(result.userId()).isEqualTo(200L);
    }

    @Test
    void 결제_확정_시_EnrollmentResult_반환() {
        final EnrollmentActionCommand command = new EnrollmentActionCommand(1L, 200L);
        when(enrollmentService.confirm(command))
                .thenReturn(new Enrollment(1L, 1L, 200L, EnrollmentStatus.CONFIRMED, null));

        final EnrollmentResult result = enrollmentUsecase.confirm(command);

        assertThat(result.status()).isEqualTo(EnrollmentStatus.CONFIRMED);
    }

    @Test
    void 수강_취소_시_EnrollmentResult_반환() {
        final EnrollmentActionCommand command = new EnrollmentActionCommand(1L, 200L);
        when(enrollmentService.cancel(command))
                .thenReturn(new Enrollment(1L, 1L, 200L, EnrollmentStatus.CANCELLED, null));

        final EnrollmentResult result = enrollmentUsecase.cancel(command);

        assertThat(result.status()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    void 내_수강_목록_조회_시_EnrollmentResult_목록_반환() {
        final Page<Enrollment> page = new PageImpl<>(List.of(pending()));
        when(enrollmentService.findMyEnrollments(200L, PageRequest.of(0, 20))).thenReturn(page);

        final Page<EnrollmentResult> result = enrollmentUsecase.findMyEnrollments(200L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).userId()).isEqualTo(200L);
        assertThat(result.getContent().get(0).status()).isEqualTo(EnrollmentStatus.PENDING);
    }
}
