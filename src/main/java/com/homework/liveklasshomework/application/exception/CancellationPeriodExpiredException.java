package com.homework.liveklasshomework.application.exception;

public class CancellationPeriodExpiredException extends RuntimeException {
    public CancellationPeriodExpiredException() {
        super("취소 기간(7일)이 초과되었습니다.");
    }
}
