package com.dongguk.graduation_be.requirement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAreaRequirementRequest {
    private Long graduationRequirementId;
    private String areaName;
    private Integer minimumCredits;
}
