package com.dongguk.graduation_be.graduation.service;

import com.dongguk.graduation_be.requirement.entity.AreaCourseGroup;
import com.dongguk.graduation_be.requirement.entity.AreaRequirement;
import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import com.dongguk.graduation_be.requirement.repository.AreaCourseGroupRepository;
import com.dongguk.graduation_be.requirement.repository.AreaRequirementRepository;
import com.dongguk.graduation_be.requirement.repository.CourseGroupItemRepository;
import com.dongguk.graduation_be.requirement.repository.GraduationRequirementRepository;
import com.dongguk.graduation_be.student.dto.TranscriptRowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupEssentialCheckService {
    private final GraduationRequirementRepository graduationRequirementRepository;
    private final AreaRequirementRepository areaRequirementRepository;
    private final AreaCourseGroupRepository areaCourseGroupRepository;
    private final CourseGroupItemRepository courseGroupItemRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> calculateEssentialMissed(
            Integer entranceYear,
            Long departmentId,
            Curriculum curriculum,
            MajorType majorType,
            List<TranscriptRowResponse> transcriptRows
    ) {
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
        if (areaRequirements.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> areaIds = areaRequirements.stream().map(AreaRequirement::getId).toList();
        List<AreaCourseGroup> targetMappings = areaCourseGroupRepository
                .findByAreaRequirementIdInAndIsEssentialTrue(areaIds);

        if (targetMappings.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> groupIds = targetMappings.stream()
                .map(mapping -> mapping.getCourseGroup().getId())
                .distinct()
                .toList();

        Map<Long, List<com.dongguk.graduation_be.requirement.entity.CourseGroupItem>> essentialItemsByGroup = courseGroupItemRepository
                .findByCourseGroupIdInAndIsEssentialTrue(groupIds)
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCourseGroup().getId()
                ));

        Set<String> completedCourseCodes = transcriptRows.stream()
                .filter(this::isActiveRecord)
                .map(TranscriptRowResponse::getCourseCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .collect(Collectors.toSet());

        Map<String, Object> missed = new LinkedHashMap<>();
        for (AreaCourseGroup mapping : targetMappings) {
            Long groupId = mapping.getCourseGroup().getId();
            List<com.dongguk.graduation_be.requirement.entity.CourseGroupItem> essentialItems =
                    essentialItemsByGroup.getOrDefault(groupId, Collections.emptyList());
            if (essentialItems.isEmpty()) {
                continue;
            }

            List<String> missingCourses = essentialItems.stream()
                    .map(item -> item.getCourse().getCourseCode() + "(" + item.getCourse().getCourseName() + ")")
                    .filter(codeAndName -> {
                        String code = codeAndName.substring(0, codeAndName.indexOf('('));
                        return !completedCourseCodes.contains(code);
                    })
                    .toList();

            if (!missingCourses.isEmpty()) {
                missed.put("group_essential:" + mapping.getCourseGroup().getGroupName(), missingCourses);
            }
        }

        return missed;
    }

    private boolean isActiveRecord(TranscriptRowResponse row) {
        String deletedGradeName = row.getDeletedGradeName();
        return deletedGradeName == null || deletedGradeName.isBlank();
    }
}
