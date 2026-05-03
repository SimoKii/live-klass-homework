package com.homework.liveklasshomework.application.enrollment.dto;

public record EnrollmentCreateCommand(
        Long klassId,
        Long userId
) {}
