package com.homework.liveklasshomework.application.klass;

import com.homework.liveklasshomework.application.exception.ForbiddenException;
import com.homework.liveklasshomework.application.exception.InvalidStatusTransitionException;
import com.homework.liveklasshomework.application.exception.ResourceNotFoundException;
import com.homework.liveklasshomework.application.klass.dto.KlassCreateCommand;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KlassServiceTest {

    @Mock
    private KlassRepository klassRepository;

    @InjectMocks
    private KlassServiceImpl klassService;

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
    void 강의_생성_시_DRAFT_상태로_생성() {
        final KlassCreateCommand command = new KlassCreateCommand(
                100L,
                "Java 입문",
                "설명",
                50000L,
                30,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );
        when(klassRepository.save(any(Klass.class))).thenAnswer(i -> i.getArgument(0));

        final Klass result = klassService.create(command);

        assertThat(result.status()).isEqualTo(KlassStatus.DRAFT);
        assertThat(result.creatorId()).isEqualTo(100L);
        assertThat(result.id()).isNull();
    }

    @Test
    void 강의_상태_DRAFT에서_OPEN으로_전이_성공() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.DRAFT)));
        when(klassRepository.save(any(Klass.class))).thenAnswer(i -> i.getArgument(0));

        final Klass result = klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        1L,
                        100L,
                        KlassStatus.OPEN
                ));

        assertThat(result.status()).isEqualTo(KlassStatus.OPEN);
    }

    @Test
    void 강의_상태_OPEN에서_CLOSED로_전이_성공() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.OPEN)));
        when(klassRepository.save(any(Klass.class))).thenAnswer(i -> i.getArgument(0));

        final Klass result = klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        1L,
                        100L,
                        KlassStatus.CLOSED
                ));

        assertThat(result.status()).isEqualTo(KlassStatus.CLOSED);
    }

    @Test
    void 강의_상태_DRAFT에서_CLOSED로_전이_거부() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.DRAFT)));

        assertThatThrownBy(() -> klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        1L,
                        100L,
                        KlassStatus.CLOSED
                )))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(klassRepository, never()).save(any());
    }

    @Test
    void 강의_상태_OPEN에서_DRAFT로_전이_거부() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.OPEN)));

        assertThatThrownBy(() -> klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        1L,
                        100L,
                        KlassStatus.DRAFT
                )))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(klassRepository, never()).save(any());
    }

    @Test
    void 강의_상태_CLOSED에서_OPEN으로_전이_거부() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.CLOSED)));

        assertThatThrownBy(() -> klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        1L,
                        100L,
                        KlassStatus.OPEN
                )))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(klassRepository, never()).save(any());
    }

    @Test
    void 강의_상태_CLOSED에서_DRAFT로_전이_거부() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.CLOSED)));

        assertThatThrownBy(() -> klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        1L,
                        100L,
                        KlassStatus.DRAFT
                )))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(klassRepository, never()).save(any());
    }

    @Test
    void 강의_상태_전이_권한_없는_요청_거부() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.DRAFT)));

        assertThatThrownBy(() -> klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        1L,
                        999L,
                        KlassStatus.OPEN
                )))
                .isInstanceOf(ForbiddenException.class);

        verify(klassRepository, never()).save(any());
    }

    @Test
    void 강의_목록_전체_조회_성공() {
        when(klassRepository.findAll())
                .thenReturn(List.of(klass(KlassStatus.DRAFT), klass(KlassStatus.OPEN)));

        final List<Klass> result = klassService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).status()).isEqualTo(KlassStatus.DRAFT);
        assertThat(result.get(1).status()).isEqualTo(KlassStatus.OPEN);
    }

    @Test
    void 강의_목록_DRAFT_상태_조회_성공() {
        when(klassRepository.findAllByStatus(KlassStatus.DRAFT))
                .thenReturn(List.of(klass(KlassStatus.DRAFT)));

        final List<Klass> result = klassService.findAllByStatus(KlassStatus.DRAFT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(KlassStatus.DRAFT);
    }

    @Test
    void 강의_목록_OPEN_상태_조회_성공() {
        when(klassRepository.findAllByStatus(KlassStatus.OPEN))
                .thenReturn(List.of(klass(KlassStatus.OPEN)));

        final List<Klass> result = klassService.findAllByStatus(KlassStatus.OPEN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(KlassStatus.OPEN);
    }

    @Test
    void 강의_목록_CLOSED_상태_조회_성공() {
        when(klassRepository.findAllByStatus(KlassStatus.CLOSED))
                .thenReturn(List.of(klass(KlassStatus.CLOSED)));

        final List<Klass> result = klassService.findAllByStatus(KlassStatus.CLOSED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(KlassStatus.CLOSED);
    }

    @Test
    void 강의_단건_조회_성공() {
        when(klassRepository.findById(1L)).thenReturn(Optional.of(klass(KlassStatus.OPEN)));

        final Klass result = klassService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void 존재하지_않는_강의_조회_시_예외_발생() {
        when(klassRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> klassService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 존재하지_않는_강의_상태_변경_시_예외_발생() {
        when(klassRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> klassService.updateStatus(
                new KlassStatusUpdateCommand(
                        999L,
                        100L,
                        KlassStatus.OPEN
                )))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(klassRepository, never()).save(any());
    }
}
