package com.dongguk.graduation_be.graduation.service;

import com.dongguk.graduation_be.student.dto.TranscriptRowResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiberalCreditCalculatorService {

    public double calculateLiberalCredits(List<TranscriptRowResponse> rows) {
        return rows.stream()
                .filter(this::isActiveRecord)
                .filter(this::isLiberalCourse)
                .mapToDouble(row -> row.getCredits() == null ? 0.0 : row.getCredits())
                .sum();
    }

    private boolean isActiveRecord(TranscriptRowResponse row) {
        String deletedGradeName = row.getDeletedGradeName();
        return deletedGradeName == null || deletedGradeName.isBlank();
    }

    private boolean isLiberalCourse(TranscriptRowResponse row) {
        String completionType = row.getCompletionType();
        if (completionType == null) {
            return true;
        }
        String normalized = completionType.replace(" ", "");
        return !normalized.startsWith("\uC804\uACF5") && !normalized.startsWith("\uBCF5\uC2181");
    }
}
