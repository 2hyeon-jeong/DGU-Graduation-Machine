package com.dongguk.graduation_be.graduation.service;

import com.dongguk.graduation_be.student.dto.TranscriptRowResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorCreditCalculatorService {

    public double calculatePrimaryMajorCredits(List<TranscriptRowResponse> rows) {
        return rows.stream()
                .filter(this::isActiveRecord)
                .filter(this::isPrimaryMajorCourse)
                .mapToDouble(row -> row.getCredits() == null ? 0.0 : row.getCredits())
                .sum();
    }

    private boolean isActiveRecord(TranscriptRowResponse row) {
        String deletedGradeName = row.getDeletedGradeName();
        return deletedGradeName == null || deletedGradeName.isBlank();
    }

    private boolean isPrimaryMajorCourse(TranscriptRowResponse row) {
        String completionType = row.getCompletionType();
        if (completionType == null) {
            return false;
        }
        String normalized = completionType.replace(" ", "");
        return normalized.startsWith("\uC804\uACF5");
    }
}
