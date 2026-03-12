package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.request.UpdateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.response.GraduationRequirementResponse;
import com.dongguk.graduation_be.requirement.entity.Curriculum;
import com.dongguk.graduation_be.requirement.entity.Department;
import com.dongguk.graduation_be.requirement.entity.GraduationRequirement;
import com.dongguk.graduation_be.requirement.entity.MajorType;
import com.dongguk.graduation_be.requirement.repository.DepartmentRepository;
import com.dongguk.graduation_be.requirement.repository.GraduationRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraduationRequirementService {
    private final GraduationRequirementRepository graduationRequirementRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Long createGraduationRequirement(CreateGraduationRequirementRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        if (graduationRequirementRepository.existsByEntranceYearAndDepartmentIdAndCurriculumAndMajorType(
                request.getEntranceYear(),
                request.getDepartmentId(),
                request.getCurriculum(),
                request.getMajorType()
        )) {
            throw new IllegalStateException("Graduation requirement already exists for this key");
        }

        GraduationRequirement graduationRequirement = GraduationRequirement.builder()
                .entranceYear(request.getEntranceYear())
                .department(department)
                .curriculum(request.getCurriculum())
                .majorType(request.getMajorType())
                .minimumCredits(request.getMinimumCredits())
                .build();

        return graduationRequirementRepository.save(graduationRequirement).getId();
    }

    @Transactional(readOnly = true)
    public List<GraduationRequirementResponse> getAllGraduationRequirements() {
        return graduationRequirementRepository.findAll().stream()
                .map(GraduationRequirementResponse::from)
                .toList();
    }

    @Transactional
    public void updateGraduationRequirement(UpdateGraduationRequirementRequest request) {
        GraduationRequirement graduationRequirement = graduationRequirementRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Graduation requirement not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        if (graduationRequirementRepository.existsByEntranceYearAndDepartmentIdAndCurriculumAndMajorTypeAndIdNot(
                request.getEntranceYear(),
                request.getDepartmentId(),
                request.getCurriculum(),
                request.getMajorType(),
                request.getId()
        )) {
            throw new IllegalStateException("Graduation requirement already exists for this key");
        }

        graduationRequirement.update(
                request.getEntranceYear(),
                department,
                request.getCurriculum(),
                request.getMajorType(),
                request.getMinimumCredits()
        );
    }

    @Transactional
    public void deleteGraduationRequirement(Long id) {
        GraduationRequirement graduationRequirement = graduationRequirementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Graduation requirement not found"));

        graduationRequirementRepository.delete(graduationRequirement);
    }

    @Transactional(readOnly = true)
    public Integer getMinimumCredits(
            Integer entranceYear,
            Long departmentId,
            Curriculum curriculum,
            MajorType majorType
    ) {
        return graduationRequirementRepository
                .findByEntranceYearAndDepartmentIdAndCurriculumAndMajorType(
                        entranceYear,
                        departmentId,
                        curriculum,
                        majorType
                )
                .map(GraduationRequirement::getMinimumCredits)
                .orElseThrow(() -> new IllegalArgumentException("Graduation requirement not found for this key"));
    }
}
