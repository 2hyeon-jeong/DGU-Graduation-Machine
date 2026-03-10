package com.dongguk.graduation_be.requirement.repository;

import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.GraduationRequirement;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, Long> {
    boolean existsByEntranceYearAndDepartmentIdAndCurriculumAndMajorType(
            Integer entranceYear,
            Long departmentId,
            Curriculum curriculum,
            MajorType majorType
    );

    boolean existsByEntranceYearAndDepartmentIdAndCurriculumAndMajorTypeAndIdNot(
            Integer entranceYear,
            Long departmentId,
            Curriculum curriculum,
            MajorType majorType,
            Long id
    );

    Optional<GraduationRequirement> findByEntranceYearAndDepartmentIdAndCurriculumAndMajorType(
            Integer entranceYear,
            Long departmentId,
            Curriculum curriculum,
            MajorType majorType
    );
}
