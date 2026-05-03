package com.homework.liveklasshomework.application.klass;

import com.homework.liveklasshomework.application.exception.ForbiddenException;
import com.homework.liveklasshomework.application.exception.InvalidStatusTransitionException;
import com.homework.liveklasshomework.application.exception.ResourceNotFoundException;
import com.homework.liveklasshomework.application.klass.dto.KlassCreateCommand;
import com.homework.liveklasshomework.application.klass.dto.KlassStatusUpdateCommand;
import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KlassServiceImpl implements KlassService {

    private final KlassRepository klassRepository;

    @Override
    @Transactional
    public Klass create(
            final KlassCreateCommand command
    ) {
        final Klass klass = new Klass(
                null,
                command.creatorId(),
                command.title(),
                command.description(),
                command.price(),
                command.maxCapacity(),
                command.startDate(),
                command.endDate(),
                KlassStatus.DRAFT
        );

        return klassRepository.save(klass);
    }

    @Override
    @Transactional
    public Klass updateStatus(
            final KlassStatusUpdateCommand command
    ) {
        final Klass klass = klassRepository.findById(command.klassId())
                .orElseThrow(() -> new ResourceNotFoundException("강의를 찾을 수 없습니다."));

        if (!klass.creatorId().equals(command.requesterId())) {
            throw new ForbiddenException();
        }

        validateKlassStatusTransition(klass.status(), command.targetStatus());

        final Klass updated = new Klass(
                klass.id(),
                klass.creatorId(),
                klass.title(),
                klass.description(),
                klass.price(),
                klass.maxCapacity(),
                klass.startDate(),
                klass.endDate(),
                command.targetStatus()
        );

        return klassRepository.save(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Klass> findAll() {
        return klassRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Klass> findAllByStatus(
            final KlassStatus status
    ) {
        return klassRepository.findAllByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Klass findById(
            final Long klassId
    ) {
        return klassRepository.findById(klassId)
                .orElseThrow(() -> new ResourceNotFoundException("강의를 찾을 수 없습니다."));
    }

    private void validateKlassStatusTransition(
            final KlassStatus current,
            final KlassStatus target
    ) {
        if (current == KlassStatus.DRAFT && target == KlassStatus.OPEN) return;
        if (current == KlassStatus.OPEN && target == KlassStatus.CLOSED) return;
        throw new InvalidStatusTransitionException(
                String.format("'%s' → '%s' 상태 전이는 허용되지 않습니다.", current, target));
    }
}
