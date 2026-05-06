package com.homework.liveklasshomework.interfaces.common;

import com.homework.liveklasshomework.application.exception.CancellationPeriodExpiredException;
import com.homework.liveklasshomework.application.exception.DuplicateEnrollmentException;
import com.homework.liveklasshomework.application.exception.EnrollmentCapacityExceededException;
import com.homework.liveklasshomework.application.exception.ForbiddenException;
import com.homework.liveklasshomework.application.exception.InvalidStatusTransitionException;
import com.homework.liveklasshomework.application.exception.KlassClosedException;
import com.homework.liveklasshomework.application.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponseDto.ErrorResponseDto handleResourceNotFound(
            final ResourceNotFoundException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponseDto.ErrorResponseDto handleForbidden(
            final ForbiddenException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
    }

    @ExceptionHandler({
            EnrollmentCapacityExceededException.class,
            DuplicateEnrollmentException.class,
            KlassClosedException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public CommonResponseDto.ErrorResponseDto handleConflict(
            final RuntimeException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
    }

    @ExceptionHandler({
            InvalidStatusTransitionException.class,
            CancellationPeriodExpiredException.class
    })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public CommonResponseDto.ErrorResponseDto handleUnprocessable(
            final RuntimeException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.UNPROCESSABLE_ENTITY,
                e.getMessage()
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponseDto.ErrorResponseDto handleMissingHeader(
            final MissingRequestHeaderException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponseDto.ErrorResponseDto handleTypeMismatch(
            final MethodArgumentTypeMismatchException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponseDto.ErrorResponseDto handleHttpMessageNotReadable(
            final HttpMessageNotReadableException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST,
                "읽을 수 없는 요청입니다."
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponseDto.ErrorResponseDto handleValidation(
            final MethodArgumentNotValidException e
    ) {
        final String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponseDto.ErrorResponseDto handleNoResourceFound(
            final NoResourceFoundException e
    ) {
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponseDto.ErrorResponseDto handleUnexpected(
            final Exception e
    ) {
        log.error("Unexpected exception", e);
        return CommonResponseDto.ErrorResponseDto.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error"
        );
    }
}
