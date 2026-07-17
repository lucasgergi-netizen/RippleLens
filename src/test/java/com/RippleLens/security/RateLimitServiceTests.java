package com.RippleLens.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class RateLimitServiceTests {

    @Test
    void boundsUniqueBucketsAndRateLimitsOverflowTraffic() {
        RateLimitService service = new RateLimitService();

        for (int index = 0; index < 20_000; index++) {
            service.check("registration", "subject-" + index, 1, Duration.ofHours(1));
        }

        service.check("registration", "overflow-1", 1, Duration.ofHours(1));
        assertThatThrownBy(() -> service.check("registration", "overflow-2", 1, Duration.ofHours(1)))
                .isInstanceOf(RateLimitExceededException.class);
        assertThat(service.bucketCountForTests()).isEqualTo(20_000);
    }

    @Test
    void remainsBoundedDuringConcurrentUniqueSubjectTraffic() throws Exception {
        RateLimitService service = new RateLimitService();
        int workers = 16;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(workers);
        ExecutorService executor = Executors.newFixedThreadPool(workers);
        try {
            for (int worker = 0; worker < workers; worker++) {
                int workerId = worker;
                executor.execute(() -> {
                    try {
                        start.await();
                        for (int attempt = 0; attempt < 2_000; attempt++) {
                            service.check("concurrent", workerId + "-" + attempt, Integer.MAX_VALUE, Duration.ofHours(1));
                        }
                    } catch (InterruptedException error) {
                        Thread.currentThread().interrupt();
                    } finally {
                        finished.countDown();
                    }
                });
            }
            start.countDown();
            assertThat(finished.await(10, TimeUnit.SECONDS)).isTrue();
            assertThat(service.bucketCountForTests()).isLessThanOrEqualTo(20_000);
        } finally {
            executor.shutdownNow();
        }
    }
}
