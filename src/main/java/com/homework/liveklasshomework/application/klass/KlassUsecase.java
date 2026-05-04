package com.homework.liveklasshomework.application.klass;

import com.homework.liveklasshomework.application.klass.dto.KlassCreateCommand;
import com.homework.liveklasshomework.application.klass.dto.KlassDetailResult;
import com.homework.liveklasshomework.application.klass.dto.KlassResult;
import com.homework.liveklasshomework.application.klass.dto.KlassStatusUpdateCommand;
import com.homework.liveklasshomework.domain.KlassStatus;
import org.springframework.lang.Nullable;

import java.util.List;

public interface KlassUsecase {

    KlassResult create(final KlassCreateCommand command);

    KlassResult updateStatus(final KlassStatusUpdateCommand command);

    List<KlassResult> findAll(@Nullable final KlassStatus status);

    KlassDetailResult findById(final Long klassId);
}
