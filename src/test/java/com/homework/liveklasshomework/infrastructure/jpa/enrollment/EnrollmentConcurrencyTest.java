package com.homework.liveklasshomework.infrastructure.jpa.enrollment;

import com.homework.liveklasshomework.application.enrollment.EnrollmentService;
import com.homework.liveklasshomework.application.enrollment.dto.EnrollmentCreateCommand;
import com.homework.liveklasshomework.application.exception.EnrollmentCapacityExceededException;
import com.homework.liveklasshomework.domain.EnrollmentStatus;
import com.homework.liveklasshomework.domain.Klass;
import com.homework.liveklasshomework.domain.KlassStatus;
import com.homework.liveklasshomework.infrastructure.jpa.klass.KlassJpaEntity;
import com.homework.liveklasshomework.infrastructure.jpa.klass.KlassJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EnrollmentConcurrencyTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private KlassJpaRepository klassJpaRepository;

    @Autowired
    private EnrollmentJpaRepository enrollmentJpaRepository;

    @AfterEach
    void cleanup() {
        enrollmentJpaRepository.deleteAll();
        klassJpaRepository.deleteAll();
    }

    @Test
    void 마지막_1자리에_5명_동시_신청_시_정확히_1명만_성공() throws InterruptedException {
        final KlassJpaEntity savedKlass = klassJpaRepository.save(
                KlassJpaEntity.from(new Klass(
                        null,
                        1L,
                        "Java 입문",
                        "설명",
                        50000L,
                        1,
                        LocalDate.now(),
                        LocalDate.now().plusMonths(1),
                        KlassStatus.OPEN
                ))
        );
        final Long klassId = savedKlass.getKlassId();

        final int threadCount = 5;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failCount = new AtomicInteger(0);
        final AtomicReference<Throwable> unexpectedException = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1L;
            final Thread thread = new Thread(() -> {
                try {
                    startLatch.await();
                    enrollmentService.enroll(new EnrollmentCreateCommand(klassId, userId));
                    successCount.incrementAndGet();
                } catch (final EnrollmentCapacityExceededException e) {
                    failCount.incrementAndGet();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (final Throwable e) {
                    unexpectedException.set(e);
                } finally {
                    doneLatch.countDown();
                }
            });
            thread.setName("enrollment-test-user-" + userId);
            thread.start();
        }

        startLatch.countDown();
        assertThat(doneLatch.await(10, TimeUnit.SECONDS))
                .as("모든 스레드가 10초 내에 완료되어야 합니다.")
                .isTrue();

        assertThat(unexpectedException.get())
                .as("예상치 못한 예외가 발생했습니다: %s", unexpectedException.get())
                .isNull();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);
        assertThat(enrollmentJpaRepository.countByKlassIdAndStatusNot(klassId, EnrollmentStatus.CANCELLED))
                .isEqualTo(1);
    }
}
