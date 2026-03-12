package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.AreaCourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaCourseGroupRepository extends JpaRepository<AreaCourseGroup, Long> {
    boolean existsByAreaRequirementIdAndCourseGroupId(Long areaRequirementId, Long courseGroupId);
    boolean existsByAreaRequirementIdAndCourseGroupIdAndIdNot(Long areaRequirementId, Long courseGroupId, Long id);
}
