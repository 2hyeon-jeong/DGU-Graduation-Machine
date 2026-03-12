package com.dongguk.graduation_be.requirement.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CourseCsvImportResponse {
    private int totalRows;
    private int createdRows;
    private int updatedRows;
    private int skippedRows;
    private int errorRows;
    private List<String> errors;
}
