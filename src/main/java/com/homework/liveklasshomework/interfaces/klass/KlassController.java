package com.homework.liveklasshomework.interfaces.klass;

import com.homework.liveklasshomework.application.klass.KlassUsecase;
import com.homework.liveklasshomework.domain.KlassStatus;
import com.homework.liveklasshomework.interfaces.common.CommonResponseDto;
import com.homework.liveklasshomework.interfaces.klass.dto.CreateKlassRequest;
import com.homework.liveklasshomework.interfaces.klass.dto.KlassDetailResponse;
import com.homework.liveklasshomework.interfaces.klass.dto.KlassResponse;
import com.homework.liveklasshomework.interfaces.klass.dto.UpdateKlassStatusRequest;
import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class KlassController {

    private final KlassUsecase klassUsecase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponseDto.SuccessResponseDto<KlassResponse> createKlass(
            @RequestHeader("X-User-Id") final Long userId,
            @Valid @RequestBody final CreateKlassRequest request
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                KlassResponse.from(klassUsecase.create(request.toCommand(userId)))
        );
    }

    @PatchMapping("/{classId}/status")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponseDto.SuccessResponseDto<KlassResponse> updateStatus(
            @PathVariable final Long classId,
            @RequestHeader("X-User-Id") final Long userId,
            @Valid @RequestBody final UpdateKlassStatusRequest request
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                KlassResponse.from(klassUsecase.updateStatus(request.toCommand(classId, userId)))
        );
    }

    @GetMapping
    public CommonResponseDto.SuccessResponseDto<List<KlassResponse>> getKlasses(
            @Nullable @RequestParam final KlassStatus status
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                klassUsecase.findAll(status)
                        .stream()
                        .map(KlassResponse::from)
                        .toList()
        );
    }

    @GetMapping("/{classId}")
    public CommonResponseDto.SuccessResponseDto<KlassDetailResponse> getKlass(
            @PathVariable final Long classId
    ) {
        return CommonResponseDto.SuccessResponseDto.success(
                KlassDetailResponse.from(klassUsecase.findById(classId))
        );
    }
}
