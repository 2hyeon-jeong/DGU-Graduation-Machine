package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateGraduationRequirementRequest;
import com.dongguk.graduation_be.requirement.entity.Department;
import com.dongguk.graduation_be.requirement.entity.GraduationRequirement;
import com.dongguk.graduation_be.requirement.repository.DepartmentRepository;
import com.dongguk.graduation_be.requirement.repository.GraduationRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GraduationRequirementService {
    private final GraduationRequirementRepository graduationRequirementRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Long createGraduationRequirement(CreateGraduationRequirementRequest request) {

        // 1. DTO에 있는 ID(Long)로 진짜 Department 객체를 찾아옵니다.
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 학과를 찾을 수 없습니다."));

        // 2. 찾아온 객체(department)를 빌더에 넣어줍니다.
        GraduationRequirement graduationRequirement = GraduationRequirement.builder()
                .entranceYear(request.getEntranceYear())
                .department(department)
                .curriculum(request.getCurriculum())
                .majorType(request.getMajorType())
                .minimumCredits(request.getMinimumCredits())
                .build();

        return graduationRequirementRepository.save(graduationRequirement).getId();
    }
}
