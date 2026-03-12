package com.dongguk.graduation_be.graduation.service;

import com.dongguk.graduation_be.graduation.dto.GraduationCreditCheckResponse;
import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import com.dongguk.graduation_be.requirement.service.GraduationRequirementService;
import com.dongguk.graduation_be.student.dto.TranscriptParseResponse;
import com.dongguk.graduation_be.student.service.StudentTranscriptService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class GraduationCreditCheckService {
    private final StudentTranscriptService studentTranscriptService;
    private final GraduationRequirementService graduationRequirementService;
    private final MajorCreditCalculatorService majorCreditCalculatorService;
    private final LiberalCreditCalculatorService liberalCreditCalculatorService;
    private final GpaCalculatorService gpaCalculatorService;

    public GraduationCreditCheckService(
            StudentTranscriptService studentTranscriptService,
            GraduationRequirementService graduationRequirementService,
            MajorCreditCalculatorService majorCreditCalculatorService,
            LiberalCreditCalculatorService liberalCreditCalculatorService,
            GpaCalculatorService gpaCalculatorService
    ) {
        this.studentTranscriptService = studentTranscriptService;
        this.graduationRequirementService = graduationRequirementService;
        this.majorCreditCalculatorService = majorCreditCalculatorService;
        this.liberalCreditCalculatorService = liberalCreditCalculatorService;
        this.gpaCalculatorService = gpaCalculatorService;
    }

    public GraduationCreditCheckResponse checkMinimumCredits(
            MultipartFile file,
            Integer entranceYear,
            Long departmentId,
            Curriculum curriculum,
            MajorType majorType
    ) {
        TranscriptParseResponse transcript = studentTranscriptService.parse(file);
        double totalCredits = transcript.getRows().stream()
                .filter(this::isActiveRecord)
                .mapToDouble(row -> row.getCredits() == null ? 0.0 : row.getCredits())
                .sum();
        double majorCredits = majorCreditCalculatorService.calculatePrimaryMajorCredits(transcript.getRows());
        double liberalCredits = liberalCreditCalculatorService.calculateLiberalCredits(transcript.getRows());
        double overallGpa = gpaCalculatorService.calculateOverallGpa(transcript.getRows());
        double majorGpa = gpaCalculatorService.calculateMajorGpa(transcript.getRows());
        double liberalGpa = gpaCalculatorService.calculateLiberalGpa(transcript.getRows());

        Integer minimumCredits = graduationRequirementService.getMinimumCredits(
                entranceYear,
                departmentId,
                curriculum,
                majorType
        );

        boolean passed = totalCredits >= minimumCredits;
        double shortage = Math.max(0.0, minimumCredits - totalCredits);
        Map<String, Double> missed = new LinkedHashMap<>();
        if (shortage > 0.0) {
            missed.put("total_credit", shortage);
        }

        return GraduationCreditCheckResponse.builder()
                .totalCredit(totalCredits)
                .majorCredit(majorCredits)
                .liberalCredit(liberalCredits)
                .overallGpa(overallGpa)
                .majorGpa(majorGpa)
                .liberalGpa(liberalGpa)
                .passed(passed)
                .missed(missed)
                .build();
    }

    private boolean isActiveRecord(com.dongguk.graduation_be.student.dto.TranscriptRowResponse row) {
        String deletedGradeName = row.getDeletedGradeName();
        return deletedGradeName == null || deletedGradeName.isBlank();
    }
}
