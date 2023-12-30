package com.example.api.service;

import com.example.api.repository.CouponRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ApplyServiceTest {


    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    public void 한번만응모(){
        applyService.apply(1L);

        final long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void 여러명응모() throws InterruptedException {
        int threadCount = 1000;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);

        // countDownLatch --> 다른 스레드에서 수행하는 작업을 기다리도록 도와주는 클래스
        final CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i = 0 ; i < threadCount ; i++){
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        final long count = couponRepository.count();
        assertThat(count).isEqualTo(100);
    }



    @Test
    public void 여러명응모_redis() throws InterruptedException {
        int threadCount = 1000;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);

        // countDownLatch --> 다른 스레드에서 수행하는 작업을 기다리도록 도와주는 클래스
        final CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i = 0 ; i < threadCount ; i++){
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.applyRedis(userId);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Thread.sleep(10_000);

        final long count = couponRepository.count();
        assertThat(count).isEqualTo(100);
    }





    @Test
    public void 여러명응모_redis_kafka() throws InterruptedException {
        int threadCount = 1000;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);

        // countDownLatch --> 다른 스레드에서 수행하는 작업을 기다리도록 도와주는 클래스
        final CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i = 0 ; i < threadCount ; i++){
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.applyKafka(userId);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Thread.sleep(5_000);

        final long count = couponRepository.count();
        assertThat(count).isEqualTo(100);
    }







}




