package com.homework.liveklasshomework.infrastructure.jpa.klass.impl;

import com.homework.liveklasshomework.application.klass.KlassRepository;
import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import com.homework.liveklasshomework.infrastructure.jpa.klass.KlassJpaEntity;
import com.homework.liveklasshomework.infrastructure.jpa.klass.KlassJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KlassRepositoryImpl implements KlassRepository {

    private final KlassJpaRepository klassJpaRepository;

    @Override
    public Klass save(
            final Klass klass
    ) {
        final KlassJpaEntity savedEntity = klassJpaRepository.save(
                KlassJpaEntity.from(klass)
        );

        return savedEntity.toDomain();
    }

    @Override
    public Optional<Klass> findById(
            final Long id
    ) {
        return klassJpaRepository.findById(id)
                .map(KlassJpaEntity::toDomain);
    }

    @Override
    public Optional<Klass> findByIdWithLock(
            final Long id
    ) {
        return klassJpaRepository.findByIdWithLock(id)
                .map(KlassJpaEntity::toDomain);
    }

    @Override
    public List<Klass> findAllByStatus(
            final KlassStatus status
    ) {
        return klassJpaRepository.findAllByStatus(status)
                .stream()
                .map(KlassJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Klass> findAll() {
        return klassJpaRepository.findAll()
                .stream()
                .map(KlassJpaEntity::toDomain)
                .toList();
    }
}
