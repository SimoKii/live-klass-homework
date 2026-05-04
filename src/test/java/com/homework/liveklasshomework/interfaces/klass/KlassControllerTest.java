package com.homework.liveklasshomework.interfaces.klass;

import com.homework.liveklasshomework.application.exception.ForbiddenException;
import com.homework.liveklasshomework.application.exception.InvalidStatusTransitionException;
import com.homework.liveklasshomework.application.exception.ResourceNotFoundException;
import com.homework.liveklasshomework.application.klass.KlassUsecase;
import com.homework.liveklasshomework.application.klass.dto.KlassDetailResult;
import com.homework.liveklasshomework.application.klass.dto.KlassResult;
import com.homework.liveklasshomework.domain.KlassStatus;
import com.homework.liveklasshomework.interfaces.klass.dto.CreateKlassRequest;
import com.homework.liveklasshomework.interfaces.klass.dto.UpdateKlassStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KlassController.class)
class KlassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KlassUsecase klassUsecase;

    private KlassResult sampleResult() {
        return new KlassResult(
                1L,
                1L,
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                KlassStatus.DRAFT
        );
    }

    @Test
    void 강의_생성_성공() throws Exception {
        when(klassUsecase.create(any())).thenReturn(sampleResult());

        final CreateKlassRequest request = new CreateKlassRequest(
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        mockMvc.perform(post("/api/v1/classes")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void X_User_Id_헤더_누락_시_예외_발생() throws Exception {
        final CreateKlassRequest request = new CreateKlassRequest(
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        mockMvc.perform(post("/api/v1/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 제목이_빈_값이면_예외_발생() throws Exception {
        final CreateKlassRequest request = new CreateKlassRequest(
                "",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        mockMvc.perform(post("/api/v1/classes")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 가격이_음수이면_예외_발생() throws Exception {
        final CreateKlassRequest request = new CreateKlassRequest(
                "Java 입문",
                "설명",
                -1L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        mockMvc.perform(post("/api/v1/classes")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 최대_수강_인원이_0_이하이면_예외_발생() throws Exception {
        final CreateKlassRequest request = new CreateKlassRequest(
                "Java 입문",
                "설명",
                50000L,
                0,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        mockMvc.perform(post("/api/v1/classes")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 시작일이_종료일_이후이면_예외_발생() throws Exception {
        final CreateKlassRequest request = new CreateKlassRequest(
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 6, 1)
        );

        mockMvc.perform(post("/api/v1/classes")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 존재하지_않는_강의_조회_시_예외_발생() throws Exception {
        when(klassUsecase.findById(999L))
                .thenThrow(new ResourceNotFoundException("강의를 찾을 수 없습니다."));

        mockMvc.perform(get("/api/v1/classes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 강의_목록_전체_조회_성공() throws Exception {
        when(klassUsecase.findAll(null)).thenReturn(List.of(sampleResult()));

        mockMvc.perform(get("/api/v1/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("DRAFT"));
    }

    @Test
    void 강의_목록_상태_필터_조회_성공() throws Exception {
        final KlassResult openResult = new KlassResult(
                1L,
                1L,
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                KlassStatus.OPEN
        );
        when(klassUsecase.findAll(KlassStatus.OPEN)).thenReturn(List.of(openResult));

        mockMvc.perform(get("/api/v1/classes").param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("OPEN"));
    }

    @Test
    void 강의_상태_변경_성공() throws Exception {
        when(klassUsecase.updateStatus(any())).thenReturn(sampleResult());

        final UpdateKlassStatusRequest request = new UpdateKlassStatusRequest(KlassStatus.OPEN);

        mockMvc.perform(patch("/api/v1/classes/1/status")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 강의_상태_변경_시_X_User_Id_헤더_누락_시_예외_발생() throws Exception {
        final UpdateKlassStatusRequest request = new UpdateKlassStatusRequest(KlassStatus.OPEN);

        mockMvc.perform(patch("/api/v1/classes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 강의_상태_변경_시_상태_값_누락_시_예외_발생() throws Exception {
        final UpdateKlassStatusRequest request = new UpdateKlassStatusRequest(null);

        mockMvc.perform(patch("/api/v1/classes/1/status")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 존재하지_않는_강의_상태_변경_시_예외_발생() throws Exception {
        when(klassUsecase.updateStatus(any()))
                .thenThrow(new ResourceNotFoundException("강의를 찾을 수 없습니다."));

        final UpdateKlassStatusRequest request = new UpdateKlassStatusRequest(KlassStatus.OPEN);

        mockMvc.perform(patch("/api/v1/classes/999/status")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 강의_상태_변경_시_권한_없는_요청_거부() throws Exception {
        when(klassUsecase.updateStatus(any()))
                .thenThrow(new ForbiddenException());

        final UpdateKlassStatusRequest request = new UpdateKlassStatusRequest(KlassStatus.OPEN);

        mockMvc.perform(patch("/api/v1/classes/1/status")
                        .header("X-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 강의_상태_변경_시_잘못된_상태_전이_거부() throws Exception {
        when(klassUsecase.updateStatus(any()))
                .thenThrow(new InvalidStatusTransitionException("'DRAFT' → 'CLOSED' 상태 전이는 허용되지 않습니다."));

        final UpdateKlassStatusRequest request = new UpdateKlassStatusRequest(KlassStatus.CLOSED);

        mockMvc.perform(patch("/api/v1/classes/1/status")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void 강의_목록_잘못된_상태_값_요청_시_예외_발생() throws Exception {
        mockMvc.perform(get("/api/v1/classes").param("status", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 강의_상세_조회_성공() throws Exception {
        final KlassDetailResult detail = new KlassDetailResult(
                1L,
                1L,
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                KlassStatus.OPEN,
                5L
        );
        when(klassUsecase.findById(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentEnrollmentCount").value(5));
    }
}
