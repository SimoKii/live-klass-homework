package com.homework.liveklasshomework.infrastructure.jpa.enrollment;

import com.homework.liveklasshomework.domain.Enrollment;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
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

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "enrollment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrollmentJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    @Column(name = "klass_id", nullable = false)
    private Long klassId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    private EnrollmentJpaEntity(
            final Long enrollmentId,
            final Long klassId,
            final Long userId,
            final EnrollmentStatus status,
            final LocalDateTime confirmedAt
    ) {
        this.enrollmentId = enrollmentId;
        this.klassId = klassId;
        this.userId = userId;
        this.status = status;
        this.confirmedAt = confirmedAt;
    }

    public static EnrollmentJpaEntity from(
            final Enrollment enrollment
    ) {
        return new EnrollmentJpaEntity(
                enrollment.id(),
                enrollment.klassId(),
                enrollment.userId(),
                enrollment.status(),
                enrollment.confirmedAt()
        );
    }

    public Enrollment toDomain() {
        return new Enrollment(
                enrollmentId,
                klassId,
                userId,
                status,
                confirmedAt
        );
    }
}
