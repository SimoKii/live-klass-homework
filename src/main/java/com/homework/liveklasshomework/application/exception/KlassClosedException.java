package com.homework.liveklasshomework.application.exception;

public class KlassClosedException extends RuntimeException {
    public KlassClosedException() {
        super("마감된 강의에는 신청할 수 없습니다.");
    }
}
