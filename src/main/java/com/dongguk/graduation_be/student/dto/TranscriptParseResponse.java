package com.dongguk.graduation_be.student.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TranscriptParseResponse {
    private int totalRows;
    private int parsedRows;
    private int errorRows;
    private List<TranscriptRowResponse> rows;
    private List<TranscriptRowErrorResponse> errors;
}
