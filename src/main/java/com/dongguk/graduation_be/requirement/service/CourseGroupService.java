package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateCourseGroupRequest;
import com.dongguk.graduation_be.requirement.dto.response.CourseGroupResponse;
import com.dongguk.graduation_be.requirement.entity.CourseGroup;
import com.dongguk.graduation_be.requirement.repository.CourseGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseGroupService {
    private final CourseGroupRepository courseGroupRepository;

    @Transactional(readOnly = true)
    public List<CourseGroupResponse> getAllCourseGroups() {
        return courseGroupRepository.findAll().stream()
                .map(CourseGroupResponse::from)
                .toList();
    }

    @Transactional
    public Long createCourseGroup(CreateCourseGroupRequest request) {
        if (courseGroupRepository.existsByGroupName(request.getGroupName())) {
            throw new IllegalStateException("CourseGroup name already exists");
        }

        CourseGroup courseGroup = CourseGroup.builder()
                .groupName(request.getGroupName())
                .build();

        return courseGroupRepository.save(courseGroup).getId();
    }

    @Transactional
    public void updateCourseGroup(UpdateCourseGroupRequest request) {
        CourseGroup courseGroup = courseGroupRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("CourseGroup not found"));

        if (courseGroupRepository.existsByGroupNameAndIdNot(request.getGroupName(), request.getId())) {
            throw new IllegalStateException("CourseGroup name already exists");
        }

        courseGroup.updateGroupName(request.getGroupName());
    }

    @Transactional
    public void deleteCourseGroup(Long id) {
        CourseGroup courseGroup = courseGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CourseGroup not found"));

        courseGroupRepository.delete(courseGroup);
    }
}
