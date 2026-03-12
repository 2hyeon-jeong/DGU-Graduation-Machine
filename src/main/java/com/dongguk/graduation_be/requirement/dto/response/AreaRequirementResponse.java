package com.dongguk.graduation_be.requirement.dto.response;

import com.dongguk.graduation_be.requirement.entity.AreaRequirement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AreaRequirementResponse {
    private Long id;
    private Long graduationRequirementId;
    private String areaName;
    private Integer minimumCredits;

    public static AreaRequirementResponse from(AreaRequirement areaRequirement) {
        return AreaRequirementResponse.builder()
                .id(areaRequirement.getId())
                .graduationRequirementId(areaRequirement.getGraduationRequirement().getId())
                .areaName(areaRequirement.getAreaName())
                .minimumCredits(areaRequirement.getMinimumCredits())
                .build();
    }
}
