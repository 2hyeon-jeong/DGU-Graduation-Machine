package com.dongguk.graduation_be.student.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranscriptParseDebugResponse {
    private String message;
    private TranscriptParseResponse data;
}
