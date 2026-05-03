package com.homework.liveklasshomework.application.exception;

public class EnrollmentCapacityExceededException extends RuntimeException {
    public EnrollmentCapacityExceededException() {
        super("강의 정원이 초과되었습니다.");
    }
}
