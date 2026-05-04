package com.homework.liveklasshomework.infrastructure.jpa.klass;

import com.homework.liveklasshomework.domain.KlassStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KlassJpaRepository extends JpaRepository<KlassJpaEntity, Long> {

    List<KlassJpaEntity> findAllByStatus(final KlassStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM KlassJpaEntity k WHERE k.klassId = :id")
    Optional<KlassJpaEntity> findByIdWithLock(@Param("id") final Long id);
}
