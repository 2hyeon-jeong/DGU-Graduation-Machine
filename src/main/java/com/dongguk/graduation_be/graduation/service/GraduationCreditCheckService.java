package com.dongguk.graduation_be.graduation.service;

import com.dongguk.graduation_be.graduation.dto.GraduationCreditCheckResponse;
import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import com.dongguk.graduation_be.requirement.service.GraduationRequirementService;
import com.dongguk.graduation_be.student.dto.TranscriptParseResponse;
import com.dongguk.graduation_be.student.service.StudentTranscriptService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GraduationCreditCheckService {
    private final StudentTranscriptService studentTranscriptService;
    private final GraduationRequirementService graduationRequirementService;

    public GraduationCreditCheckService(
            StudentTranscriptService studentTranscriptService,
            GraduationRequirementService graduationRequirementService
    ) {
        this.studentTranscriptService = studentTranscriptService;
        this.graduationRequirementService = graduationRequirementService;
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
                .mapToDouble(row -> row.getCredits() == null ? 0.0 : row.getCredits())
                .sum();

        Integer minimumCredits = graduationRequirementService.getMinimumCredits(
                entranceYear,
                departmentId,
                curriculum,
                majorType
        );

        boolean passed = totalCredits >= minimumCredits;

        return GraduationCreditCheckResponse.builder()
                .totalCredit(totalCredits)
                .minimumCredit(minimumCredits)
                .passedMinimumCredit(passed)
                .build();
    }
}
