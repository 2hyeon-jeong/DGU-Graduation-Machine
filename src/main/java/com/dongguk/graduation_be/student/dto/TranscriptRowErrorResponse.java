package com.dongguk.graduation_be.student.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranscriptRowErrorResponse {
    private Integer rowNumber;
    private String reason;
}
