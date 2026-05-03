package com.homework.liveklasshomework.application.klass;

import com.homework.liveklasshomework.application.enrollment.EnrollmentService;
import com.homework.liveklasshomework.application.klass.dto.KlassCreateCommand;
import com.homework.liveklasshomework.application.klass.dto.KlassDetailResult;
import com.homework.liveklasshomework.application.klass.dto.KlassResult;
import com.homework.liveklasshomework.application.klass.dto.KlassStatusUpdateCommand;
import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KlassUsecaseImpl implements KlassUsecase {

    private final KlassService klassService;
    private final EnrollmentService enrollmentService;

    @Override
    public KlassResult create(
            final KlassCreateCommand command
    ) {
        return KlassResult.from(klassService.create(command));
    }

    @Override
    public KlassResult updateStatus(
            final KlassStatusUpdateCommand command
    ) {
        return KlassResult.from(klassService.updateStatus(command));
    }

    @Override
    public List<KlassResult> findAll() {
        return klassService.findAll().stream()
                .map(KlassResult::from)
                .toList();
    }

    @Override
    public List<KlassResult> findAllByStatus(
            final KlassStatus status
    ) {
        return klassService.findAllByStatus(status).stream()
                .map(KlassResult::from)
                .toList();
    }

    @Override
    public KlassDetailResult findById(
            final Long klassId
    ) {
        final Klass klass = klassService.findById(klassId);
        final long count = enrollmentService.countNonCancelledEnrollments(klassId);

        return KlassDetailResult.of(klass, count);
    }
}
