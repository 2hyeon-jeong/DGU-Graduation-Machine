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
public class GroupMinCountCheckService {
    private final GraduationRequirementRepository graduationRequirementRepository;
    private final AreaRequirementRepository areaRequirementRepository;
    private final AreaCourseGroupRepository areaCourseGroupRepository;
    private final CourseGroupItemRepository courseGroupItemRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> calculateGroupMinCountMissed(
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
        List<AreaCourseGroup> essentialMappings = areaCourseGroupRepository
                .findByAreaRequirementIdInAndIsEssentialTrue(areaIds)
                .stream()
                .filter(mapping -> mapping.getMinCount() != null && mapping.getMinCount() > 0)
                .toList();

        if (essentialMappings.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> groupIds = essentialMappings.stream()
                .map(mapping -> mapping.getCourseGroup().getId())
                .distinct()
                .toList();

        Map<Long, Set<String>> groupCourseCodes = courseGroupItemRepository.findByCourseGroupIdIn(groupIds).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCourseGroup().getId(),
                        Collectors.mapping(item -> item.getCourse().getCourseCode(), Collectors.toSet())
                ));
        Map<Long, Set<String>> essentialCodesByGroup = courseGroupItemRepository
                .findByCourseGroupIdInAndIsEssentialTrue(groupIds)
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCourseGroup().getId(),
                        Collectors.mapping(item -> item.getCourse().getCourseCode(), Collectors.toSet())
                ));

        Set<String> completedCourseCodes = transcriptRows.stream()
                .filter(this::isActiveRecord)
                .map(TranscriptRowResponse::getCourseCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .collect(Collectors.toSet());

        Map<String, Object> missed = new LinkedHashMap<>();
        for (AreaCourseGroup mapping : essentialMappings) {
            Long groupId = mapping.getCourseGroup().getId();
            // If a group has essential-course rules, emit only group_essential to avoid duplicated failure reasons.
            if (!essentialCodesByGroup.getOrDefault(groupId, Collections.emptySet()).isEmpty()) {
                continue;
            }
            Set<String> expectedCourseCodes = groupCourseCodes.getOrDefault(groupId, Collections.emptySet());

            long completedCount = expectedCourseCodes.stream()
                    .filter(completedCourseCodes::contains)
                    .count();

            int shortage = (int) Math.max(0, mapping.getMinCount() - completedCount);
            if (shortage > 0) {
                String key = "group_min_count:" + mapping.getCourseGroup().getGroupName();
                missed.put(key, shortage);
            }
        }

        return missed;
    }

    private boolean isActiveRecord(TranscriptRowResponse row) {
        String deletedGradeName = row.getDeletedGradeName();
        return deletedGradeName == null || deletedGradeName.isBlank();
    }
}
