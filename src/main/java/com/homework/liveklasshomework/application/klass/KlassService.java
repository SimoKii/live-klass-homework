package com.homework.liveklasshomework.application.klass;

import com.homework.liveklasshomework.application.klass.dto.KlassCreateCommand;
import com.homework.liveklasshomework.application.klass.dto.KlassStatusUpdateCommand;
import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import java.util.List;

public interface KlassService {

    Klass create(final KlassCreateCommand command);

    Klass updateStatus(final KlassStatusUpdateCommand command);

    List<Klass> findAll();

    List<Klass> findAllByStatus(final KlassStatus status);

    Klass findById(final Long klassId);
}
