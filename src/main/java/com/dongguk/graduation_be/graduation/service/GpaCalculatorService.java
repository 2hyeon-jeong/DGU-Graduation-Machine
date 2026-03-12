package com.dongguk.graduation_be.graduation.service;

import com.dongguk.graduation_be.student.dto.TranscriptRowResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GpaCalculatorService {

    public Double calculateOverallGpa(List<TranscriptRowResponse> rows) {
        return calculate(rows, false, false);
    }

    public Double calculateMajorGpa(List<TranscriptRowResponse> rows) {
        return calculate(rows, true, false);
    }

    public Double calculateLiberalGpa(List<TranscriptRowResponse> rows) {
        return calculate(rows, false, true);
    }

    private Double calculate(List<TranscriptRowResponse> rows, boolean majorOnly, boolean liberalOnly) {
        double totalGradePoints = 0.0;
        double totalCredits = 0.0;

        for (TranscriptRowResponse row : rows) {
            if (!isActiveRecord(row)) {
                continue;
            }
            if (majorOnly && !isMajorCompletionType(row.getCompletionType())) {
                continue;
            }
            if (liberalOnly && !isLiberalCompletionType(row.getCompletionType())) {
                continue;
            }

            Double credit = row.getCredits();
            Double gradePoint = toGradePoint(row.getGrade());
            if (credit == null || credit <= 0 || gradePoint == null) {
                continue;
            }

            totalGradePoints += gradePoint * credit;
            totalCredits += credit;
        }

        if (totalCredits == 0.0) {
            return 0.0;
        }
        return round2(totalGradePoints / totalCredits);
    }

    private boolean isMajorCompletionType(String completionType) {
        if (completionType == null) {
            return false;
        }
        String normalized = completionType.replace(" ", "");
        return normalized.startsWith("\uC804\uACF5");
    }

    private boolean isLiberalCompletionType(String completionType) {
        if (completionType == null) {
            return true;
        }
        String normalized = completionType.replace(" ", "");
        return !normalized.startsWith("\uC804\uACF5") && !normalized.startsWith("\uBCF5\uC2181");
    }

    private boolean isActiveRecord(TranscriptRowResponse row) {
        String deletedGradeName = row.getDeletedGradeName();
        return deletedGradeName == null || deletedGradeName.isBlank();
    }

    private Double toGradePoint(String grade) {
        if (grade == null) {
            return null;
        }

        String normalized = grade.trim().toUpperCase().replace('\uFF0B', '+');
        return switch (normalized) {
            case "A+" -> 4.5;
            case "A0", "A" -> 4.0;
            case "B+" -> 3.5;
            case "B0", "B" -> 3.0;
            case "C+" -> 2.5;
            case "C0", "C" -> 2.0;
            case "D+" -> 1.5;
            case "D0", "D" -> 1.0;
            case "F" -> 0.0;
            case "P", "NP", "N", "S", "U", "W", "PASS" -> null;
            default -> null;
        };
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
