package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.GraduationRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, Long> {
}
