package com.homework.liveklasshomework.interfaces.enrollment;

import com.homework.liveklasshomework.application.enrollment.EnrollmentUsecase;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentResult;
import com.homework.liveklasshomework.application.exception.CancellationPeriodExpiredException;
import com.homework.liveklasshomework.application.exception.DuplicateEnrollmentException;
import com.homework.liveklasshomework.application.exception.EnrollmentCapacityExceededException;
import com.homework.liveklasshomework.application.exception.ForbiddenException;
import com.homework.liveklasshomework.application.exception.InvalidStatusTransitionException;
import com.homework.liveklasshomework.application.exception.KlassClosedException;
import com.homework.liveklasshomework.application.exception.ResourceNotFoundException;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentUsecase enrollmentUsecase;

    private EnrollmentResult pendingResult() {
        return new EnrollmentResult(
                1L,
                1L,
                200L,
                EnrollmentStatus.PENDING,
                null
        );
    }

    private EnrollmentResult confirmedResult() {
        return new EnrollmentResult(
                1L,
                1L,
                200L,
                EnrollmentStatus.CONFIRMED,
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
    }

    private EnrollmentResult cancelledResult() {
        return new EnrollmentResult(
                1L,
                1L,
                200L,
                EnrollmentStatus.CANCELLED,
                null
        );
    }

    @Test
    void 수강_신청_성공() throws Exception {
        when(enrollmentUsecase.enroll(any())).thenReturn(pendingResult());

        mockMvc.perform(post("/api/v1/classes/1/enrollments")
                        .header("X-User-Id", 200L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 수강_신청_시_X_User_Id_헤더_누락_시_예외_발생() throws Exception {
        mockMvc.perform(post("/api/v1/classes/1/enrollments"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 존재하지_않는_강의_수강_신청_시_예외_발생() throws Exception {
        when(enrollmentUsecase.enroll(any()))
                .thenThrow(new ResourceNotFoundException("강의를 찾을 수 없습니다."));

        mockMvc.perform(post("/api/v1/classes/999/enrollments")
                        .header("X-User-Id", 200L))
                .andExpect(status().isNotFound());
    }

    @Test
    void 중복_수강_신청_시_예외_발생() throws Exception {
        when(enrollmentUsecase.enroll(any()))
                .thenThrow(new DuplicateEnrollmentException());

        mockMvc.perform(post("/api/v1/classes/1/enrollments")
                        .header("X-User-Id", 200L))
                .andExpect(status().isConflict());
    }

    @Test
    void 정원_초과_수강_신청_시_예외_발생() throws Exception {
        when(enrollmentUsecase.enroll(any()))
                .thenThrow(new EnrollmentCapacityExceededException());

        mockMvc.perform(post("/api/v1/classes/1/enrollments")
                        .header("X-User-Id", 200L))
                .andExpect(status().isConflict());
    }

    @Test
    void 종료된_강의_수강_신청_시_예외_발생() throws Exception {
        when(enrollmentUsecase.enroll(any()))
                .thenThrow(new KlassClosedException());

        mockMvc.perform(post("/api/v1/classes/1/enrollments")
                        .header("X-User-Id", 200L))
                .andExpect(status().isConflict());
    }

    @Test
    void 준비중인_강의_수강_신청_시_예외_발생() throws Exception {
        when(enrollmentUsecase.enroll(any()))
                .thenThrow(new InvalidStatusTransitionException("OPEN 상태의 강의에만 신청할 수 있습니다."));

        mockMvc.perform(post("/api/v1/classes/1/enrollments")
                        .header("X-User-Id", 200L))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void 결제_확정_성공() throws Exception {
        when(enrollmentUsecase.confirm(any())).thenReturn(confirmedResult());

        mockMvc.perform(patch("/api/v1/enrollments/1/confirm")
                        .header("X-User-Id", 200L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.confirmedAt").value("2026-06-01T10:00:00"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 결제_확정_시_X_User_Id_헤더_누락_시_예외_발생() throws Exception {
        mockMvc.perform(patch("/api/v1/enrollments/1/confirm"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 존재하지_않는_수강_신청_결제_확정_시_예외_발생() throws Exception {
        when(enrollmentUsecase.confirm(any()))
                .thenThrow(new ResourceNotFoundException("수강 신청을 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/v1/enrollments/999/confirm")
                        .header("X-User-Id", 200L))
                .andExpect(status().isNotFound());
    }

    @Test
    void 결제_확정_시_권한_없는_요청_거부() throws Exception {
        when(enrollmentUsecase.confirm(any()))
                .thenThrow(new ForbiddenException());

        mockMvc.perform(patch("/api/v1/enrollments/1/confirm")
                        .header("X-User-Id", 999L))
                .andExpect(status().isForbidden());
    }

    @Test
    void 결제_확정_시_잘못된_상태_전이_거부() throws Exception {
        when(enrollmentUsecase.confirm(any()))
                .thenThrow(new InvalidStatusTransitionException("'CANCELLED' → 'CONFIRMED' 상태 전이는 허용되지 않습니다."));

        mockMvc.perform(patch("/api/v1/enrollments/1/confirm")
                        .header("X-User-Id", 200L))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void 수강_취소_성공() throws Exception {
        when(enrollmentUsecase.cancel(any())).thenReturn(cancelledResult());

        mockMvc.perform(patch("/api/v1/enrollments/1/cancel")
                        .header("X-User-Id", 200L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 수강_취소_시_X_User_Id_헤더_누락_시_예외_발생() throws Exception {
        mockMvc.perform(patch("/api/v1/enrollments/1/cancel"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 존재하지_않는_수강_신청_취소_시_예외_발생() throws Exception {
        when(enrollmentUsecase.cancel(any()))
                .thenThrow(new ResourceNotFoundException("수강 신청을 찾을 수 없습니다."));

        mockMvc.perform(patch("/api/v1/enrollments/999/cancel")
                        .header("X-User-Id", 200L))
                .andExpect(status().isNotFound());
    }

    @Test
    void 수강_취소_시_권한_없는_요청_거부() throws Exception {
        when(enrollmentUsecase.cancel(any()))
                .thenThrow(new ForbiddenException());

        mockMvc.perform(patch("/api/v1/enrollments/1/cancel")
                        .header("X-User-Id", 999L))
                .andExpect(status().isForbidden());
    }

    @Test
    void 수강_취소_시_잘못된_상태_전이_거부() throws Exception {
        when(enrollmentUsecase.cancel(any()))
                .thenThrow(new InvalidStatusTransitionException("'CANCELLED' → 'CANCELLED' 상태 전이는 허용되지 않습니다."));

        mockMvc.perform(patch("/api/v1/enrollments/1/cancel")
                        .header("X-User-Id", 200L))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void 수강_취소_시_취소_기간_초과_거부() throws Exception {
        when(enrollmentUsecase.cancel(any()))
                .thenThrow(new CancellationPeriodExpiredException());

        mockMvc.perform(patch("/api/v1/enrollments/1/cancel")
                        .header("X-User-Id", 200L))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void 내_수강_목록_조회_성공() throws Exception {
        when(enrollmentUsecase.findMyEnrollments(eq(200L), any()))
                .thenReturn(new PageImpl<>(List.of(pendingResult()), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/enrollments/me")
                        .header("X-User-Id", 200L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 내_수강_목록_조회_시_X_User_Id_헤더_누락_시_예외_발생() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 수강_신청_시_X_User_Id_가_음수이면_예외_발생() throws Exception {
        mockMvc.perform(post("/api/v1/classes/1/enrollments")
                        .header("X-User-Id", -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 결제_확정_시_X_User_Id_가_음수이면_예외_발생() throws Exception {
        mockMvc.perform(patch("/api/v1/enrollments/1/confirm")
                        .header("X-User-Id", -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 수강_취소_시_X_User_Id_가_음수이면_예외_발생() throws Exception {
        mockMvc.perform(patch("/api/v1/enrollments/1/cancel")
                        .header("X-User-Id", -1L))
                .andExpect(status().isBadRequest());
    }
}
