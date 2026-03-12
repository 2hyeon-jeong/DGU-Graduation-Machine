package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateAreaCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateAreaCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.response.AreaCourseGroupResponse;
import com.dongguk.graduation_be.requirement.entity.AreaCourseGroup;
import com.dongguk.graduation_be.requirement.entity.AreaRequirement;
import com.dongguk.graduation_be.requirement.entity.CourseGroup;
import com.dongguk.graduation_be.requirement.repository.AreaCourseGroupRepository;
import com.dongguk.graduation_be.requirement.repository.AreaRequirementRepository;
import com.dongguk.graduation_be.requirement.repository.CourseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaCourseGroupService {
    private final AreaCourseGroupRepository areaCourseGroupRepository;
    private final AreaRequirementRepository areaRequirementRepository;
    private final CourseGroupRepository courseGroupRepository;

    @Transactional(readOnly = true)
    public List<AreaCourseGroupResponse> getAllAreaCourseGroups() {
        return areaCourseGroupRepository.findAll().stream()
                .map(AreaCourseGroupResponse::from)
                .toList();
    }

    @Transactional
    public Long createAreaCourseGroup(CreateAreaCourseGroupRequest request) {
        AreaRequirement areaRequirement = areaRequirementRepository.findById(request.getAreaRequirementId())
                .orElseThrow(() -> new IllegalArgumentException("AreaRequirement not found"));
        CourseGroup courseGroup = courseGroupRepository.findById(request.getCourseGroupId())
                .orElseThrow(() -> new IllegalArgumentException("CourseGroup not found"));

        if (areaCourseGroupRepository.existsByAreaRequirementIdAndCourseGroupId(
                request.getAreaRequirementId(), request.getCourseGroupId())) {
            throw new IllegalStateException("AreaCourseGroup mapping already exists");
        }

        AreaCourseGroup areaCourseGroup = AreaCourseGroup.builder()
                .areaRequirement(areaRequirement)
                .courseGroup(courseGroup)
                .isEssential(request.getIsEssential())
                .minCount(request.getMinCount())
                .build();

        return areaCourseGroupRepository.save(areaCourseGroup).getId();
    }

    @Transactional
    public void updateAreaCourseGroup(UpdateAreaCourseGroupRequest request) {
        AreaCourseGroup areaCourseGroup = areaCourseGroupRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("AreaCourseGroup not found"));
        AreaRequirement areaRequirement = areaRequirementRepository.findById(request.getAreaRequirementId())
                .orElseThrow(() -> new IllegalArgumentException("AreaRequirement not found"));
        CourseGroup courseGroup = courseGroupRepository.findById(request.getCourseGroupId())
                .orElseThrow(() -> new IllegalArgumentException("CourseGroup not found"));

        if (areaCourseGroupRepository.existsByAreaRequirementIdAndCourseGroupIdAndIdNot(
                request.getAreaRequirementId(), request.getCourseGroupId(), request.getId())) {
            throw new IllegalStateException("AreaCourseGroup mapping already exists");
        }

        areaCourseGroup.update(
                areaRequirement,
                courseGroup,
                request.getIsEssential(),
                request.getMinCount()
        );
    }

    @Transactional
    public void deleteAreaCourseGroup(Long id) {
        AreaCourseGroup areaCourseGroup = areaCourseGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("AreaCourseGroup not found"));
        areaCourseGroupRepository.delete(areaCourseGroup);
    }
}
