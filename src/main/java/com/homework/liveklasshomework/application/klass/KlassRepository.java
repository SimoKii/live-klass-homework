package com.homework.liveklasshomework.application.klass;

import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;

import java.util.List;
import java.util.Optional;

public interface KlassRepository {

    Klass save(final Klass klass);

    Optional<Klass> findById(final Long id);

    /**
     * PESSIMISTIC_WRITE 락을 획득하여 강의를 조회합니다.
     * 동시 수강 신청의 정원 초과 방지를 위해 사용합니다.
     */
    Optional<Klass> findByIdWithLock(final Long id);

    List<Klass> findAllByStatus(final KlassStatus status);

    List<Klass> findAll();
}
