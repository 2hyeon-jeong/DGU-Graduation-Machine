package com.dongguk.graduation_be.requirement.service;

import com.dongguk.graduation_be.requirement.dto.request.CreateAreaRequirementRequest;
import com.dongguk.graduation_be.requirement.dto.response.AreaRequirementResponse;
import com.dongguk.graduation_be.requirement.entity.AreaRequirement;
import com.dongguk.graduation_be.requirement.entity.GraduationRequirement;
import com.dongguk.graduation_be.requirement.repository.AreaRequirementRepository;
import com.dongguk.graduation_be.requirement.repository.GraduationRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaRequirementService {
    private final AreaRequirementRepository areaRequirementRepository;
    private final GraduationRequirementRepository graduationRequirementRepository;

    @Transactional
    public Long createAreaRequirement(CreateAreaRequirementRequest request) {
        GraduationRequirement graduationRequirement = graduationRequirementRepository
                .findById(request.getGraduationRequirementId())
                .orElseThrow(() -> new IllegalArgumentException("Graduation requirement not found"));

        if (areaRequirementRepository.existsByGraduationRequirementIdAndAreaName(
                request.getGraduationRequirementId(),
                request.getAreaName()
        )) {
            throw new IllegalStateException("Area requirement already exists in this graduation requirement");
        }

        AreaRequirement areaRequirement = AreaRequirement.builder()
                .graduationRequirement(graduationRequirement)
                .areaName(request.getAreaName())
                .minimumCredits(request.getMinimumCredits())
                .build();

        return areaRequirementRepository.save(areaRequirement).getId();
    }

    @Transactional(readOnly = true)
    public List<AreaRequirementResponse> getAllAreaRequirements() {
        return areaRequirementRepository.findAll().stream()
                .map(AreaRequirementResponse::from)
                .toList();
    }
}
