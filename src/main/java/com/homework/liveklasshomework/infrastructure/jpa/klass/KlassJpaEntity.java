package com.homework.liveklasshomework.infrastructure.jpa.klass;

import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import com.homework.liveklasshomework.infrastructure.jpa.base.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "klass")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KlassJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "klass_id", nullable = false)
    private Long klassId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private KlassStatus status;

    private KlassJpaEntity(
            final Long klassId,
            final Long creatorId,
            final String title,
            final String description,
            final Long price,
            final int maxCapacity,
            final LocalDate startDate,
            final LocalDate endDate,
            final KlassStatus status
    ) {
        this.klassId = klassId;
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.maxCapacity = maxCapacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public static KlassJpaEntity from(
            final Klass klass
    ) {
        return new KlassJpaEntity(
                klass.id(),
                klass.creatorId(),
                klass.title(),
                klass.description(),
                klass.price(),
                klass.maxCapacity(),
                klass.startDate(),
                klass.endDate(),
                klass.status()
        );
    }

    public Klass toDomain() {
        return new Klass(
                klassId,
                creatorId,
                title,
                description,
                price,
                maxCapacity,
                startDate,
                endDate,
                status
        );
    }
}
