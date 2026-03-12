package com.dongguk.graduation_be.graduation.service;

import com.dongguk.graduation_be.graduation.dto.GraduationCreditCheckResponse;
import com.dongguk.graduation_be.requirement.entity.AreaRequirement;
import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import com.dongguk.graduation_be.requirement.repository.AreaRequirementRepository;
import com.dongguk.graduation_be.requirement.repository.GraduationRequirementRepository;
import com.dongguk.graduation_be.requirement.service.GraduationRequirementService;
import com.dongguk.graduation_be.student.dto.TranscriptParseResponse;
import com.dongguk.graduation_be.student.service.StudentTranscriptService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GraduationCreditCheckService {
    private final StudentTranscriptService studentTranscriptService;
    private final GraduationRequirementService graduationRequirementService;
    private final MajorCreditCalculatorService majorCreditCalculatorService;
    private final LiberalCreditCalculatorService liberalCreditCalculatorService;
    private final GpaCalculatorService gpaCalculatorService;
    private final GroupMinCountCheckService groupMinCountCheckService;
    private final GraduationRequirementRepository graduationRequirementRepository;
    private final AreaRequirementRepository areaRequirementRepository;

    public GraduationCreditCheckService(
            StudentTranscriptService studentTranscriptService,
            GraduationRequirementService graduationRequirementService,
            MajorCreditCalculatorService majorCreditCalculatorService,
            LiberalCreditCalculatorService liberalCreditCalculatorService,
            GpaCalculatorService gpaCalculatorService,
            GroupMinCountCheckService groupMinCountCheckService,
            GraduationRequirementRepository graduationRequirementRepository,
            AreaRequirementRepository areaRequirementRepository
    ) {
        this.studentTranscriptService = studentTranscriptService;
        this.graduationRequirementService = graduationRequirementService;
        this.majorCreditCalculatorService = majorCreditCalculatorService;
        this.liberalCreditCalculatorService = liberalCreditCalculatorService;
        this.gpaCalculatorService = gpaCalculatorService;
        this.groupMinCountCheckService = groupMinCountCheckService;
        this.graduationRequirementRepository = graduationRequirementRepository;
        this.areaRequirementRepository = areaRequirementRepository;
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

        Long graduationRequirementId = graduationRequirementRepository
                .findByEntranceYearAndDepartmentIdAndCurriculumAndMajorType(
                        entranceYear,
                        departmentId,
                        curriculum,
                        majorType
                )
                .orElseThrow(() -> new IllegalArgumentException("Graduation requirement not found for this key"))
                .getId();

        List<AreaRequirement> areaRequirements = areaRequirementRepository.findByGraduationRequirementId(graduationRequirementId);

        Optional<AreaRequirement> majorArea = areaRequirements.stream()
                .filter(area -> "전공".equals(normalize(area.getAreaName())))
                .findFirst();
        if (majorArea.isPresent()) {
            double majorShortage = Math.max(0.0, majorArea.get().getMinimumCredits() - majorCredits);
            if (majorShortage > 0.0) {
                missed.put("major_credit", majorShortage);
            }
        }

        Optional<AreaRequirement> liberalArea = areaRequirements.stream()
                .filter(area -> {
                    String name = normalize(area.getAreaName());
                    return "공통교양".equals(name) || "교양".equals(name);
                })
                // prefer "공통교양" when both exist
                .sorted(Comparator.comparingInt(area -> "공통교양".equals(normalize(area.getAreaName())) ? 0 : 1))
                .findFirst();
        if (liberalArea.isPresent()) {
            double liberalShortage = Math.max(0.0, liberalArea.get().getMinimumCredits() - liberalCredits);
            if (liberalShortage > 0.0) {
                missed.put("liberal_credit", liberalShortage);
            }
        }

        missed.putAll(groupMinCountCheckService.calculateGroupMinCountMissed(
                entranceYear,
                departmentId,
                curriculum,
                majorType,
                transcript.getRows()
        ));
        passed = missed.isEmpty();

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

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(" ", "").trim();
    }
}
