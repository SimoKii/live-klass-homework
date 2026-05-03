package com.homework.liveklasshomework.application.exception;

public class DuplicateEnrollmentException extends RuntimeException {
    public DuplicateEnrollmentException() {
        super("이미 신청한 강의입니다.");
    }
}
