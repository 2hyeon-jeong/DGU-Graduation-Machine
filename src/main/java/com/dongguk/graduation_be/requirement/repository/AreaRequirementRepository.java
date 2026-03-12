package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.AreaRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRequirementRepository extends JpaRepository<AreaRequirement, Long> {
    boolean existsByGraduationRequirementIdAndAreaName(Long graduationRequirementId, String areaName);
}
