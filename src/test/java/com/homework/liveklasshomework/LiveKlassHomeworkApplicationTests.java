package com.homework.liveklasshomework;

import com.homework.liveklasshomework.application.enrollment.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class LiveKlassHomeworkApplicationTests {

    @MockitoBean
    private EnrollmentRepository enrollmentRepository;

    @Test
    void contextLoads() {
    }
}
