package com.homework.liveklasshomework.interfaces.common;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

@UtilityClass
public class CommonResponseDto {

    public record SuccessResponseDto<T>(
            String message,
            T data
    ) {
        private static final String SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다.";

        public static <T> SuccessResponseDto<T> success(
                final T data
        ) {
            return new SuccessResponseDto<>(
                    SUCCESS_MESSAGE,
                    data
            );
        }
    }

    public record ErrorResponseDto(
            int code,
            String message
    ) {
        public static ErrorResponseDto of(
                final HttpStatus httpStatus,
                final String message
        ) {
            return new ErrorResponseDto(
                    httpStatus.value(),
                    message
            );
        }
    }
}
